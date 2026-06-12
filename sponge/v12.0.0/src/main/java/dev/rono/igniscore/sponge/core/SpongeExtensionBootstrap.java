package dev.rono.igniscore.sponge.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.sponge.loader.SpongeBlockExtensionLoader;
import dev.rono.igniscore.sponge.loader.SpongeItemExtensionLoader;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;
import dev.rono.igniscore.sponge.service.SpongeItemManager;

@Singleton
public class SpongeExtensionBootstrap {
    private final SpongeBlockExtensionLoader blockExtensionLoader;
    private final SpongeItemExtensionLoader itemExtensionLoader;
    private final SpongeBlockManager blockManager;
    private final SpongeItemManager itemManager;

    @Inject
    public SpongeExtensionBootstrap(SpongeBlockExtensionLoader blockExtensionLoader,
                                      SpongeItemExtensionLoader itemExtensionLoader,
                                      SpongeBlockManager blockManager,
                                      SpongeItemManager itemManager) {
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
        blockManager.loadFromExtensions(blockExtensionLoader.loadAll());
    }

    public void reloadItems() {
        itemManager.loadFromExtensions(itemExtensionLoader.loadAll());
    }
}
