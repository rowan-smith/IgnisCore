package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.IgnisApiVersion;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Singleton
final class ExtensionLoadEngine {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final BundledExtensionExtractor bundledExtractor;

    @Inject
    ExtensionLoadEngine(Main plugin,
                        IgnisStrategyRegistry strategyRegistry,
                        IgnisStrategyContext strategyContext,
                        BundledExtensionExtractor bundledExtractor) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.bundledExtractor = bundledExtractor;
    }

    List<LoadedExtension<BlockDefinition>> loadBlocks() {
        File folder = prepareFolder(ExtensionKind.BLOCK);
        if (folder == null) {
            return List.of();
        }

        List<LoadedExtension<BlockDefinition>> loaded = new ArrayList<>();
        int modelData = ExtensionKind.BLOCK.modelDataStart();
        for (File jar : listJars(folder, ExtensionKind.BLOCK)) {
            try {
                loaded.add(loadBlockJar(jar, modelData++));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load block extension " + jar.getName() + ": " + e.getMessage());
            }
        }
        return List.copyOf(loaded);
    }

    List<LoadedExtension<ItemDefinition>> loadItems() {
        File folder = prepareFolder(ExtensionKind.ITEM);
        if (folder == null) {
            return List.of();
        }

        List<LoadedExtension<ItemDefinition>> loaded = new ArrayList<>();
        int modelData = ExtensionKind.ITEM.modelDataStart();
        for (File jar : listJars(folder, ExtensionKind.ITEM)) {
            try {
                loaded.add(loadItemJar(jar, modelData++));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load item extension " + jar.getName() + ": " + e.getMessage());
            }
        }
        return List.copyOf(loaded);
    }

    void unload(List<? extends LoadedExtension<?>> extensions) {
        for (LoadedExtension<?> extension : extensions) {
            strategyRegistry.unregisterBySource(extension.getManifest().getId());
            try {
                extension.getClassLoader().close();
            } catch (Exception ignored) {
            }
        }
    }

    private File prepareFolder(ExtensionKind kind) {
        File folder = new File(plugin.getDataFolder(), kind.folderName());
        bundledExtractor.extractBundled(kind.bundledResourcePrefix(), folder);

        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning("Could not create " + kind.folderName() + " folder at " + folder.getAbsolutePath());
            return null;
        }
        return folder;
    }

    private List<File> listJars(File folder, ExtensionKind kind) {
        File[] jars = folder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            plugin.getLogger().info("No " + kind.folderName() + " extension JARs found in " + folder.getAbsolutePath());
            return List.of();
        }
        return List.of(jars);
    }

    private LoadedExtension<BlockDefinition> loadBlockJar(File jarFile, int modelData) throws Exception {
        ExtensionManifest manifest = readManifest(jarFile, ExtensionKind.BLOCK);
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        BlockDefinition definition = ExtensionKind.BLOCK.parseBlock(config,
                config.getString("id", manifest.getId()), modelData, manifest.getId());
        return loadExtension(jarFile, manifest, descriptor, definition, ExtensionKind.BLOCK);
    }

    private LoadedExtension<ItemDefinition> loadItemJar(File jarFile, int modelData) throws Exception {
        ExtensionManifest manifest = readManifest(jarFile, ExtensionKind.ITEM);
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        ItemDefinition definition = ExtensionKind.ITEM.parseItem(config,
                config.getString("id", manifest.getId()), modelData, manifest.getId());
        return loadExtension(jarFile, manifest, descriptor, definition, ExtensionKind.ITEM);
    }

    private ExtensionManifest readManifest(File jarFile, ExtensionKind kind) throws Exception {
        return ExtensionJarSupport.readManifest(jarFile, kind.manifestFileName(),
                input -> ExtensionManifest.fromStream(input, kind.manifestFileName()));
    }

    private <D> LoadedExtension<D> loadExtension(File jarFile,
                                                 ExtensionManifest manifest,
                                                 IgnisStrategyDescriptor descriptor,
                                                 D definition,
                                                 ExtensionKind kind) throws Exception {
        IgnisApiVersion.requireCompatible(manifest.getApiVersion(), manifest.getId());

        String strategyId = descriptor.getId();
        String definitionId = definitionIdFor(definition);

        URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, plugin.getClass().getClassLoader());
        ExtensionResources resources = new ExtensionResources(classLoader);

        try {
            ExtensionJarSupport.loadStrategy(classLoader, manifest.getStrategyClass(), strategyContext,
                    strategyRegistry, descriptor, kind);

            if (!strategyRegistry.isRegistered(strategyId)) {
                throw new IllegalStateException(kind.folderName() + " extension " + manifest.getId()
                        + " did not register strategy class " + manifest.getStrategyClass());
            }

            plugin.debug("Loaded " + kind.folderName() + " extension '" + manifest.getName() + "' v"
                    + manifest.getVersion() + " (" + definitionId + ") from " + jarFile.getName());
            return new LoadedExtension<>(manifest, jarFile, classLoader, definition, resources);
        } catch (Exception e) {
            classLoader.close();
            throw e;
        }
    }

    private static String definitionIdFor(Object definition) {
        if (definition instanceof BlockDefinition blockDefinition) {
            return blockDefinition.getId();
        }
        if (definition instanceof ItemDefinition itemDefinition) {
            return itemDefinition.getId();
        }
        throw new IllegalStateException("Unsupported definition type: " + definition.getClass().getName());
    }
}
