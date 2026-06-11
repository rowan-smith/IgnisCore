package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.extension.IgnisItemPlugin;
import dev.rono.igniscore.api.extension.ItemExtensionContext;
import dev.rono.igniscore.api.extension.ItemExtensionManifest;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.DefinitionParser;
import dev.rono.igniscore.model.ItemDefinition;
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
public class ItemExtensionLoader {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final ExtensionResourceProvider resourceProvider;
    private final BundledExtensionExtractor bundledExtractor;
    private final List<LoadedItemExtension> loadedExtensions = new ArrayList<>();

    @Inject
    public ItemExtensionLoader(Main plugin,
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

    public List<LoadedItemExtension> loadAll() {
        unloadAll();

        File itemsFolder = new File(plugin.getDataFolder(), "items");
        bundledExtractor.extractBundledItems(itemsFolder);

        if (!itemsFolder.exists() && !itemsFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create items folder at " + itemsFolder.getAbsolutePath());
            resourceProvider.setItemExtensions(List.of());
            return List.copyOf(loadedExtensions);
        }

        File[] jars = itemsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            plugin.getLogger().info("No item extension JARs found in " + itemsFolder.getAbsolutePath());
            resourceProvider.setItemExtensions(List.of());
            return List.copyOf(loadedExtensions);
        }

        int modelData = 20001;
        for (File jar : jars) {
            try {
                loadedExtensions.add(loadJar(jar, modelData++));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load item extension " + jar.getName() + ": " + e.getMessage());
            }
        }

        resourceProvider.setItemExtensions(loadedExtensions);
        return List.copyOf(loadedExtensions);
    }

    public void unloadAll() {
        for (LoadedItemExtension extension : loadedExtensions) {
            try {
                ItemExtensionContext context = createContext(extension.getManifest(), extension.getItemDefinition(),
                        extension.getResources());
                extension.getPlugin().onUnload(context);
                strategyRegistry.unregisterBySource(extension.getManifest().getId());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to unload item extension " + extension.getManifest().getId()
                        + ": " + e.getMessage());
            }

            try {
                extension.getClassLoader().close();
            } catch (Exception ignored) {
            }
        }
        loadedExtensions.clear();
        resourceProvider.setItemExtensions(List.of());
    }

    public List<LoadedItemExtension> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }

    private LoadedItemExtension loadJar(File jarFile, int modelData) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                plugin.getClass().getClassLoader()
        );

        ItemExtensionManifest manifest = readManifest(jarFile);
        ItemDefinition itemDefinition = readItemDefinition(jarFile, manifest.getId(), modelData);
        ExtensionResources resources = new ExtensionResources(classLoader);

        Class<?> mainClass = Class.forName(manifest.getMainClass(), true, classLoader);
        Object instance = mainClass.getDeclaredConstructor().newInstance();
        if (!(instance instanceof IgnisItemPlugin itemPlugin)) {
            classLoader.close();
            throw new IllegalStateException(manifest.getMainClass() + " does not implement IgnisItemPlugin");
        }

        ItemExtensionContext context = createContext(manifest, itemDefinition, resources);
        itemPlugin.onLoad(context);

        if (!strategyRegistry.isRegistered(itemDefinition.getStrategy())) {
            classLoader.close();
            throw new IllegalStateException("Item extension " + manifest.getId()
                    + " did not register strategy '" + itemDefinition.getStrategy() + "'");
        }

        plugin.getLogger().info("Loaded item extension '" + manifest.getName() + "' v" + manifest.getVersion()
                + " (" + itemDefinition.getId() + ") from " + jarFile.getName());
        return new LoadedItemExtension(manifest, jarFile, classLoader, itemPlugin, itemDefinition, resources);
    }

    private ItemExtensionContext createContext(ItemExtensionManifest manifest,
                                               ItemDefinition itemDefinition,
                                               ExtensionResources resources) {
        return new ItemExtensionContext(manifest, itemDefinition, strategyRegistry, strategyContext, resources);
    }

    private ItemExtensionManifest readManifest(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("item-extension.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing item-extension.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return ItemExtensionManifest.fromStream(inputStream);
            }
        }
    }

    private ItemDefinition readItemDefinition(File jarFile, String extensionId, int modelData) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("config.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String fallbackId = config.getString("id", extensionId);
                return DefinitionParser.parseItem(config, fallbackId, modelData, extensionId);
            }
        }
    }
}
