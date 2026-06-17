package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.IgnisPlatformIntegration;
import dev.rono.igniscore.api.port.ResourcePackHost;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;

import java.util.logging.Logger;

@Singleton
public class IgnisRuntimeLifecycle {
    private final ExtensionBootstrap extensionBootstrap;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ExtensionSupportService extensionSupportService;
    private final BlockManager blockManager;
    private final PlacedBlockPersistenceService placedBlockPersistenceService;
    private final ResourcePackHost resourcePackHost;
    private final IgnisPlatformIntegration platformIntegration;
    private final Logger logger;

    @Inject
    public IgnisRuntimeLifecycle(ExtensionBootstrap extensionBootstrap,
                                   BlockExtensionLoader blockExtensionLoader,
                                   ItemExtensionLoader itemExtensionLoader,
                                   ExtensionSupportService extensionSupportService,
                                   BlockManager blockManager,
                                   PlacedBlockPersistenceService placedBlockPersistenceService,
                                   ResourcePackHost resourcePackHost,
                                   IgnisPlatformIntegration platformIntegration,
                                   dev.rono.igniscore.api.port.PlatformAdapter platformAdapter) {
        this.extensionBootstrap = extensionBootstrap;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.extensionSupportService = extensionSupportService;
        this.blockManager = blockManager;
        this.placedBlockPersistenceService = placedBlockPersistenceService;
        this.resourcePackHost = resourcePackHost;
        this.platformIntegration = platformIntegration;
        this.logger = platformAdapter.getLogger();
    }

    public void enable() {
        platformIntegration.registerCommands();
        extensionBootstrap.loadAll();
        platformIntegration.onRuntimeEnable();
        initializeResourcePack();
    }

    public void disable() {
        blockExtensionLoader.unloadAll();
        itemExtensionLoader.unloadAll();
        extensionSupportService.clear();
        blockManager.cleanup();
        placedBlockPersistenceService.shutdown();
        resourcePackHost.stopServer();
        platformIntegration.onRuntimeDisable();
    }

    private void initializeResourcePack() {
        resourcePackHost.buildAndRegisterAsync(
                resourcePackHost::startServer,
                error -> logger.severe("Failed to generate resource pack: " + error.getMessage()));
    }
}
