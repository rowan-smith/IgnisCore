package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;

@Singleton
public class ExtensionBootstrap {
    private final Main plugin;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final BlockManager blockManager;
    private final ItemManager itemManager;

    @Inject
    public ExtensionBootstrap(Main plugin,
                              BlockExtensionLoader blockExtensionLoader,
                              ItemExtensionLoader itemExtensionLoader,
                              BlockManager blockManager,
                              ItemManager itemManager) {
        this.plugin = plugin;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
    }

    public void loadAll() {
        reloadBlocks();
        reloadItems();
    }

    public void reloadAll() {
        blockExtensionLoader.unloadAll();
        itemExtensionLoader.unloadAll();
        loadAll();
    }

    public void reloadBlocks() {
        blockExtensionLoader.loadAll();
        blockManager.loadFromExtensions(blockExtensionLoader.getLoadedExtensions());
        plugin.getLogger().info("Loaded " + blockExtensionLoader.getLoadedExtensions().size() + " block extension(s).");
    }

    public void reloadItems() {
        itemExtensionLoader.loadAll();
        itemManager.loadFromExtensions(itemExtensionLoader.getLoadedExtensions());
        plugin.getLogger().info("Loaded " + itemExtensionLoader.getLoadedExtensions().size() + " item extension(s).");
    }
}
