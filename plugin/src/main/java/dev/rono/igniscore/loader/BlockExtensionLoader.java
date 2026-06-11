package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.extension.BlockExtensionContext;
import dev.rono.igniscore.api.extension.BlockExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.extension.IgnisBlockPlugin;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.DefinitionParser;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            try {
                BlockExtensionContext context = createContext(extension.getManifest(), extension.getBlockDefinition(),
                        extension.getResources());
                extension.getPlugin().onUnload(context);
                strategyRegistry.unregisterBySource(extension.getManifest().getId());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to unload block extension " + extension.getManifest().getId()
                        + ": " + e.getMessage());
            }

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
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                plugin.getClass().getClassLoader()
        );

        BlockExtensionManifest manifest = readManifest(jarFile);
        BlockDefinition blockDefinition = readBlockDefinition(jarFile, manifest.getId(), modelData);
        ExtensionResources resources = new ExtensionResources(classLoader);

        Class<?> mainClass = Class.forName(manifest.getMainClass(), true, classLoader);
        Object instance = mainClass.getDeclaredConstructor().newInstance();
        if (!(instance instanceof IgnisBlockPlugin blockPlugin)) {
            classLoader.close();
            throw new IllegalStateException(manifest.getMainClass() + " does not implement IgnisBlockPlugin");
        }

        BlockExtensionContext context = createContext(manifest, blockDefinition, resources);
        blockPlugin.onLoad(context);

        if (!strategyRegistry.isRegistered(blockDefinition.getStrategy())) {
            classLoader.close();
            throw new IllegalStateException("Block extension " + manifest.getId()
                    + " did not register strategy '" + blockDefinition.getStrategy() + "'");
        }

        plugin.getLogger().info("Loaded block extension '" + manifest.getName() + "' v" + manifest.getVersion()
                + " (" + blockDefinition.getId() + ") from " + jarFile.getName());
        return new LoadedBlockExtension(manifest, jarFile, classLoader, blockPlugin, blockDefinition, resources);
    }

    private BlockExtensionContext createContext(BlockExtensionManifest manifest,
                                                BlockDefinition blockDefinition,
                                                ExtensionResources resources) {
        return new BlockExtensionContext(manifest, blockDefinition, strategyRegistry, strategyContext, resources);
    }

    private BlockExtensionManifest readManifest(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("block-extension.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing block-extension.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return BlockExtensionManifest.fromStream(inputStream);
            }
        }
    }

    private BlockDefinition readBlockDefinition(File jarFile, String extensionId, int modelData) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("config.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String fallbackId = config.getString("id", extensionId);
                return DefinitionParser.parseBlock(config, fallbackId, modelData, extensionId);
            }
        }
    }
}
