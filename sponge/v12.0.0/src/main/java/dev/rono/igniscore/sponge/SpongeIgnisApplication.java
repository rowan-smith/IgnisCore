package dev.rono.igniscore.sponge;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;

import java.util.List;

public class SpongeIgnisApplication {
    private final PlatformAdapter platformAdapter;
    private final ExtensionBootstrap extensionBootstrap;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final SpongeBlockManager blockManager;
    private final SpongeItemListener itemListener;
    private final SpongeBlockListener blockListener;

    @Inject
    public SpongeIgnisApplication(PlatformAdapter platformAdapter,
                                  ExtensionBootstrap extensionBootstrap,
                                  BlockExtensionLoader blockExtensionLoader,
                                  ItemExtensionLoader itemExtensionLoader,
                                  SpongeBlockManager blockManager,
                                  SpongeItemListener itemListener,
                                  SpongeBlockListener blockListener) {
        this.platformAdapter = platformAdapter;
        this.extensionBootstrap = extensionBootstrap;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockManager = blockManager;
        this.itemListener = itemListener;
        this.blockListener = blockListener;
    }

    public void enable() {
        extensionBootstrap.loadAll();
        platformAdapter.registerEventListeners(List.of(itemListener, blockListener));
    }

    public void disable() {
        blockExtensionLoader.unloadAll();
        itemExtensionLoader.unloadAll();
        blockManager.cleanup();
    }
}
