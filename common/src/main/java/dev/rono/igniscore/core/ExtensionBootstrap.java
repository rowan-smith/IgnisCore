package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.manager.ItemManager;

@Singleton
public class ExtensionBootstrap {
    private final IgnisRuntimeHost host;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final BlockTypeRegistry blockTypeRegistry;
    private final ItemManager itemManager;

    @Inject
    public ExtensionBootstrap(IgnisRuntimeHost host,
                              BlockExtensionLoader blockExtensionLoader,
                              ItemExtensionLoader itemExtensionLoader,
                              BlockTypeRegistry blockTypeRegistry,
                              ItemManager itemManager) {
        this.host = host;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockTypeRegistry = blockTypeRegistry;
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
        blockTypeRegistry.loadFromExtensions(blockExtensionLoader.getLoadedExtensions());
        host.getLogger().info("Loaded " + blockExtensionLoader.getLoadedExtensions().size() + " block extension(s).");
    }

    public void reloadItems() {
        itemExtensionLoader.loadAll();
        itemManager.loadFromExtensions(itemExtensionLoader.getLoadedExtensions());
        host.getLogger().info("Loaded " + itemExtensionLoader.getLoadedExtensions().size() + " item extension(s).");
    }
}
