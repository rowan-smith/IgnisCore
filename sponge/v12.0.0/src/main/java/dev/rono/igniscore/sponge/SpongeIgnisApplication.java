package dev.rono.igniscore.sponge;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.core.SpongeExtensionBootstrap;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.loader.SpongeBlockExtensionLoader;
import dev.rono.igniscore.sponge.loader.SpongeItemExtensionLoader;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;

import java.util.List;

public class SpongeIgnisApplication {
    private final PlatformAdapter platformAdapter;
    private final SpongeExtensionBootstrap extensionBootstrap;
    private final SpongeBlockExtensionLoader blockExtensionLoader;
    private final SpongeItemExtensionLoader itemExtensionLoader;
    private final SpongeBlockManager blockManager;
    private final SpongeItemListener itemListener;
    private final SpongeBlockListener blockListener;

    @Inject
    public SpongeIgnisApplication(PlatformAdapter platformAdapter,
                                  SpongeExtensionBootstrap extensionBootstrap,
                                  SpongeBlockExtensionLoader blockExtensionLoader,
                                  SpongeItemExtensionLoader itemExtensionLoader,
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
