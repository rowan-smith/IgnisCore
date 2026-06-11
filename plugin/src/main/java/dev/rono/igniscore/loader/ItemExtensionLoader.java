package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.extension.ItemExtensionManifest;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.DefinitionParser;
import dev.rono.igniscore.model.ItemDefinition;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
            strategyRegistry.unregisterBySource(extension.getManifest().getId());
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
        URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, plugin.getClass().getClassLoader());
        ItemExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "item-extension.yml",
                ItemExtensionManifest::fromStream);
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        ItemDefinition itemDefinition = DefinitionParser.parseItem(config,
                config.getString("id", manifest.getId()), modelData, manifest.getId());
        ExtensionResources resources = new ExtensionResources(classLoader);

        ExtensionJarSupport.loadStrategy(classLoader, manifest.getStrategyClass(), strategyContext,
                strategyRegistry, manifest.getId());

        if (!strategyRegistry.isRegistered(itemDefinition.getStrategy())) {
            classLoader.close();
            throw new IllegalStateException("Item extension " + manifest.getId()
                    + " strategy '" + itemDefinition.getStrategy() + "' was not registered");
        }

        plugin.getLogger().info("Loaded item extension '" + manifest.getName() + "' v" + manifest.getVersion()
                + " (" + itemDefinition.getId() + ") from " + jarFile.getName());
        return new LoadedItemExtension(manifest, jarFile, classLoader, itemDefinition, resources);
    }
}
