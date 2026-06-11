package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.loader.ContentPackLoader;
import dev.rono.igniscore.loader.StrategyPluginLoader;
import dev.rono.igniscore.manager.BlockManager;

@Singleton
public class ExtensionBootstrap {
    private final Main plugin;
    private final BuiltinStrategyBootstrap builtinStrategyBootstrap;
    private final StrategyPluginLoader strategyPluginLoader;
    private final ContentPackLoader contentPackLoader;
    private final BlockManager blockManager;

    @Inject
    public ExtensionBootstrap(Main plugin,
                              BuiltinStrategyBootstrap builtinStrategyBootstrap,
                              StrategyPluginLoader strategyPluginLoader,
                              ContentPackLoader contentPackLoader,
                              BlockManager blockManager) {
        this.plugin = plugin;
        this.builtinStrategyBootstrap = builtinStrategyBootstrap;
        this.strategyPluginLoader = strategyPluginLoader;
        this.contentPackLoader = contentPackLoader;
        this.blockManager = blockManager;
    }

    public void loadAll() {
        builtinStrategyBootstrap.registerAll();
        strategyPluginLoader.loadAll();
        contentPackLoader.loadAll();
        blockManager.loadConfig();
        plugin.getLogger().info("Loaded " + strategyPluginLoader.getLoadedPlugins().size()
                + " strategy plugin(s) and " + contentPackLoader.getLoadedPacks().size() + " content pack(s).");
    }

    public void reloadAll() {
        strategyPluginLoader.unloadAll();
        loadAll();
    }
}
