package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.strategy.IgnisStrategyPlugin;

import java.net.URLClassLoader;

public final class LoadedStrategyPlugin {
    private final StrategyPluginManifest manifest;
    private final IgnisStrategyPlugin plugin;
    private final URLClassLoader classLoader;

    public LoadedStrategyPlugin(StrategyPluginManifest manifest, IgnisStrategyPlugin plugin, URLClassLoader classLoader) {
        this.manifest = manifest;
        this.plugin = plugin;
        this.classLoader = classLoader;
    }

    public StrategyPluginManifest getManifest() {
        return manifest;
    }

    public IgnisStrategyPlugin getPlugin() {
        return plugin;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
