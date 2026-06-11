package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyPlugin;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Singleton
public class StrategyPluginLoader {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final List<LoadedStrategyPlugin> loadedPlugins = new ArrayList<>();

    @Inject
    public StrategyPluginLoader(Main plugin,
                                IgnisStrategyRegistry strategyRegistry,
                                IgnisStrategyContext strategyContext) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
    }

    public List<LoadedStrategyPlugin> loadAll() {
        unloadAll();

        File strategiesFolder = new File(plugin.getDataFolder(), "strategies");
        if (!strategiesFolder.exists() && !strategiesFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create strategies folder at " + strategiesFolder.getAbsolutePath());
            return List.copyOf(loadedPlugins);
        }

        File[] jars = strategiesFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            plugin.getLogger().info("No drop-in strategy plugins found in " + strategiesFolder.getAbsolutePath());
            return List.copyOf(loadedPlugins);
        }

        for (File jar : jars) {
            try {
                loadedPlugins.add(loadJar(jar));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load strategy plugin " + jar.getName() + ": " + e.getMessage());
            }
        }

        return List.copyOf(loadedPlugins);
    }

    public void unloadAll() {
        for (LoadedStrategyPlugin loadedPlugin : loadedPlugins) {
            try {
                loadedPlugin.getPlugin().onUnload(strategyRegistry);
                strategyRegistry.unregisterBySource(loadedPlugin.getManifest().getId());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to unload strategy plugin " + loadedPlugin.getManifest().getId()
                        + ": " + e.getMessage());
            }

            try {
                loadedPlugin.getClassLoader().close();
            } catch (Exception ignored) {
            }
        }
        loadedPlugins.clear();
    }

    public List<LoadedStrategyPlugin> getLoadedPlugins() {
        return List.copyOf(loadedPlugins);
    }

    private LoadedStrategyPlugin loadJar(File jarFile) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL()},
                plugin.getClass().getClassLoader()
        );

        StrategyPluginManifest manifest = readManifest(jarFile);
        Class<?> mainClass = Class.forName(manifest.getMainClass(), true, classLoader);
        Object instance = mainClass.getDeclaredConstructor().newInstance();
        if (!(instance instanceof IgnisStrategyPlugin strategyPlugin)) {
            classLoader.close();
            throw new IllegalStateException(manifest.getMainClass() + " does not implement IgnisStrategyPlugin");
        }

        strategyPlugin.onLoad(strategyRegistry, strategyContext);
        plugin.getLogger().info("Loaded strategy plugin '" + manifest.getName() + "' v" + manifest.getVersion()
                + " from " + jarFile.getName());
        return new LoadedStrategyPlugin(manifest, strategyPlugin, classLoader);
    }

    private StrategyPluginManifest readManifest(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("strategy-plugin.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing strategy-plugin.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return StrategyPluginManifest.fromStream(inputStream);
            }
        }
    }
}
