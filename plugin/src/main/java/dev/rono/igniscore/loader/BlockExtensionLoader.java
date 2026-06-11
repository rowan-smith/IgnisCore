package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.extension.BlockExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.DefinitionParser;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BlockExtensionLoader {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final ExtensionResourceProvider resourceProvider;
    private final BundledExtensionExtractor bundledExtractor;
    private final List<LoadedBlockExtension> loadedExtensions = new ArrayList<>();

    @Inject
    public BlockExtensionLoader(Main plugin,
                                IgnisStrategyRegistry strategyRegistry,
                                IgnisStrategyContext strategyContext,
                                ExtensionResourceProvider resourceProvider,
                                BundledExtensionExtractor bundledExtractor) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.resourceProvider = resourceProvider;
        this.bundledExtractor = bundledExtractor;
    }

    public List<LoadedBlockExtension> loadAll() {
        unloadAll();

        File blocksFolder = new File(plugin.getDataFolder(), "blocks");
        bundledExtractor.extractBundledBlocks(blocksFolder);

        if (!blocksFolder.exists() && !blocksFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create blocks folder at " + blocksFolder.getAbsolutePath());
            resourceProvider.setBlockExtensions(List.of());
            return List.copyOf(loadedExtensions);
        }

        File[] jars = blocksFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            plugin.getLogger().info("No block extension JARs found in " + blocksFolder.getAbsolutePath());
            resourceProvider.setBlockExtensions(List.of());
            return List.copyOf(loadedExtensions);
        }

        int modelData = 10001;
        for (File jar : jars) {
            try {
                loadedExtensions.add(loadJar(jar, modelData++));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load block extension " + jar.getName() + ": " + e.getMessage());
            }
        }

        resourceProvider.setBlockExtensions(loadedExtensions);
        return List.copyOf(loadedExtensions);
    }

    public void unloadAll() {
        for (LoadedBlockExtension extension : loadedExtensions) {
            strategyRegistry.unregisterBySource(extension.getManifest().getId());
            try {
                extension.getClassLoader().close();
            } catch (Exception ignored) {
            }
        }
        loadedExtensions.clear();
        resourceProvider.setBlockExtensions(List.of());
    }

    public List<LoadedBlockExtension> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }

    private LoadedBlockExtension loadJar(File jarFile, int modelData) throws Exception {
        URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, plugin.getClass().getClassLoader());
        BlockExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                BlockExtensionManifest::fromStream);
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        BlockDefinition blockDefinition = DefinitionParser.parseBlock(config,
                config.getString("id", manifest.getId()), modelData, manifest.getId());
        ExtensionResources resources = new ExtensionResources(classLoader);

        ExtensionJarSupport.loadStrategy(classLoader, manifest.getStrategyClass(), strategyContext,
                strategyRegistry, manifest.getId());

        if (!strategyRegistry.isRegistered(blockDefinition.getStrategy())) {
            classLoader.close();
            throw new IllegalStateException("Block extension " + manifest.getId()
                    + " strategy '" + blockDefinition.getStrategy() + "' was not registered");
        }

        plugin.getLogger().info("Loaded block extension '" + manifest.getName() + "' v" + manifest.getVersion()
                + " (" + blockDefinition.getId() + ") from " + jarFile.getName());
        return new LoadedBlockExtension(manifest, jarFile, classLoader, blockDefinition, resources);
    }
}
