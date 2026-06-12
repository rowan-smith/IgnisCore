package dev.rono.igniscore.sponge.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.IgnisApiVersion;
import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class SpongeExtensionLoadEngine {
    private final PlatformAdapter platformAdapter;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final SpongeBundledExtensionExtractor bundledExtractor;

    @Inject
    SpongeExtensionLoadEngine(PlatformAdapter platformAdapter,
                              IgnisStrategyRegistry strategyRegistry,
                              IgnisStrategyContext strategyContext,
                              SpongeBundledExtensionExtractor bundledExtractor) {
        this.platformAdapter = platformAdapter;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.bundledExtractor = bundledExtractor;
    }

    List<SpongeLoadedExtension<BlockDefinition>> loadBlocks() {
        File folder = prepareFolder(SpongeExtensionKind.BLOCK);
        if (folder == null) {
            return List.of();
        }

        List<SpongeLoadedExtension<BlockDefinition>> loaded = new ArrayList<>();
        int modelData = SpongeExtensionKind.BLOCK.modelDataStart();
        for (File jar : listJars(folder, SpongeExtensionKind.BLOCK)) {
            try {
                loaded.add(loadBlockJar(jar, modelData++));
            } catch (Exception e) {
                platformAdapter.getLogger().severe("Failed to load block extension " + jar.getName() + ": " + e.getMessage());
            }
        }
        return List.copyOf(loaded);
    }

    List<SpongeLoadedExtension<ItemDefinition>> loadItems() {
        File folder = prepareFolder(SpongeExtensionKind.ITEM);
        if (folder == null) {
            return List.of();
        }

        List<SpongeLoadedExtension<ItemDefinition>> loaded = new ArrayList<>();
        int modelData = SpongeExtensionKind.ITEM.modelDataStart();
        for (File jar : listJars(folder, SpongeExtensionKind.ITEM)) {
            try {
                loaded.add(loadItemJar(jar, modelData++));
            } catch (Exception e) {
                platformAdapter.getLogger().severe("Failed to load item extension " + jar.getName() + ": " + e.getMessage());
            }
        }
        return List.copyOf(loaded);
    }

    void unload(List<? extends SpongeLoadedExtension<?>> extensions) {
        for (SpongeLoadedExtension<?> extension : extensions) {
            strategyRegistry.unregisterBySource(extension.getManifest().getId());
            try {
                extension.getClassLoader().close();
            } catch (Exception ignored) {
            }
        }
    }

    private File prepareFolder(SpongeExtensionKind kind) {
        File folder = platformAdapter.getDataDirectory().resolve(kind.folderName()).toFile();
        bundledExtractor.extractBundled(kind.bundledResourcePrefix(), folder);

        if (!folder.exists() && !folder.mkdirs()) {
            platformAdapter.getLogger().warning("Could not create " + kind.folderName() + " folder at " + folder.getAbsolutePath());
            return null;
        }
        return folder;
    }

    private List<File> listJars(File folder, SpongeExtensionKind kind) {
        File[] jars = folder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            platformAdapter.getLogger().info("No " + kind.folderName() + " extension JARs found in " + folder.getAbsolutePath());
            return List.of();
        }
        return List.of(jars);
    }

    private SpongeLoadedExtension<BlockDefinition> loadBlockJar(File jarFile, int modelData) throws Exception {
        ExtensionManifest manifest = readManifest(jarFile, SpongeExtensionKind.BLOCK);
        Map<String, Object> config = SpongeExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        BlockDefinition definition = SpongeExtensionKind.BLOCK.parseBlock(config, manifest.getId(), modelData, manifest.getId());
        return loadExtension(jarFile, manifest, descriptor, definition, SpongeExtensionKind.BLOCK);
    }

    private SpongeLoadedExtension<ItemDefinition> loadItemJar(File jarFile, int modelData) throws Exception {
        ExtensionManifest manifest = readManifest(jarFile, SpongeExtensionKind.ITEM);
        Map<String, Object> config = SpongeExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        ItemDefinition definition = SpongeExtensionKind.ITEM.parseItem(config, manifest.getId(), modelData, manifest.getId());
        return loadExtension(jarFile, manifest, descriptor, definition, SpongeExtensionKind.ITEM);
    }

    private ExtensionManifest readManifest(File jarFile, SpongeExtensionKind kind) throws Exception {
        return SpongeExtensionJarSupport.readManifest(jarFile, kind.manifestFileName(),
                input -> ExtensionManifest.fromStream(input, kind.manifestFileName()));
    }

    private <D> SpongeLoadedExtension<D> loadExtension(File jarFile,
                                                       ExtensionManifest manifest,
                                                       IgnisStrategyDescriptor descriptor,
                                                       D definition,
                                                       SpongeExtensionKind kind) throws Exception {
        IgnisApiVersion.requireCompatible(manifest.getApiVersion(), manifest.getId());

        String strategyId = descriptor.getId();
        String definitionId = definitionIdFor(definition);

        URLClassLoader classLoader = SpongeExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader());
        ExtensionResources resources = new ExtensionResources(classLoader);

        try {
            SpongeExtensionJarSupport.loadStrategy(classLoader, manifest.getStrategyClass(), strategyContext,
                    strategyRegistry, descriptor, kind);

            if (!strategyRegistry.isRegistered(strategyId)) {
                throw new IllegalStateException(kind.folderName() + " extension " + manifest.getId()
                        + " did not register strategy class " + manifest.getStrategyClass());
            }

            platformAdapter.getLogger().info("Loaded " + kind.folderName() + " extension '" + manifest.getName() + "' v"
                    + manifest.getVersion() + " (" + definitionId + ") from " + jarFile.getName());
            return new SpongeLoadedExtension<>(manifest, jarFile, classLoader, definition, resources);
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
