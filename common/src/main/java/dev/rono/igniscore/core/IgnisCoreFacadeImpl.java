package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.IgnisCoreFacade;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.ResourcePackHost;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.service.ExtensionSupportService;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class IgnisCoreFacadeImpl implements IgnisCoreFacade {
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final IgnisCustomItemFactory itemFactory;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionBootstrap extensionBootstrap;
    private final ResourcePackHost resourcePackHost;
    private final PlatformAdapter platformAdapter;
    private final IgnisEventBusImpl eventBus;
    private final Logger logger;

    @Inject
    public IgnisCoreFacadeImpl(BlockManager blockManager,
                               ItemManager itemManager,
                               IgnisCustomItemFactory itemFactory,
                               IgnisStrategyRegistry strategyRegistry,
                               IgnisNbtService nbtService,
                               IgnisProtocolService protocolService,
                               IgnisEffectService effectService,
                               ExtensionBootstrap extensionBootstrap,
                               ResourcePackHost resourcePackHost,
                               PlatformAdapter platformAdapter,
                               IgnisEventBusImpl eventBus) {
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.itemFactory = itemFactory;
        this.strategyRegistry = strategyRegistry;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionBootstrap = extensionBootstrap;
        this.resourcePackHost = resourcePackHost;
        this.platformAdapter = platformAdapter;
        this.eventBus = eventBus;
        this.logger = platformAdapter.getLogger();
    }

    @Override
    public Map<String, BlockDefinition> getBlockTypes() {
        return blockManager.getBlockTypes();
    }

    @Override
    public Map<String, ItemDefinition> getItemTypes() {
        return itemManager.getItemTypes();
    }

    @Override
    public RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        return blockManager.triggerBlock(location, typeId, context);
    }

    @Override
    public RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context) {
        String typeId = blockManager.getPlacedBlockType(location);
        if (typeId == null) {
            return null;
        }

        blockManager.unregisterPlacedBlock(location);
        platformAdapter.clearBlock(location);
        return blockManager.triggerBlock(location, typeId, context);
    }

    @Override
    public String getPlacedBlockType(IgnisLocation location) {
        return blockManager.getPlacedBlockType(location);
    }

    @Override
    public Collection<RuntimeBlockInstance> getActiveBlocks() {
        return blockManager.getActiveBlocks();
    }

    @Override
    public IgnisItem createBlockItem(String typeId) {
        return itemFactory.createBlockItem(typeId);
    }

    @Override
    public IgnisItem createItem(String typeId) {
        return itemFactory.createItem(typeId);
    }

    @Override
    public IgnisStrategyRegistry getStrategyRegistry() {
        return strategyRegistry;
    }

    @Override
    public IgnisNbtService getNbtService() {
        return nbtService;
    }

    @Override
    public IgnisProtocolService getProtocolService() {
        return protocolService;
    }

    @Override
    public IgnisEffectService getEffectService() {
        return effectService;
    }

    @Override
    public IgnisEventBus eventBus() {
        return eventBus;
    }

    @Override
    public void reloadExtensions() {
        extensionBootstrap.reloadAll();
        resourcePackHost.buildAndRegisterAsync(
                () -> {
                    try {
                        resourcePackHost.reloadConfiguration();
                    } catch (RuntimeException error) {
                        logger.severe("Failed to reload resource pack configuration: " + error.getMessage());
                    }
                },
                error -> logger.severe("Failed to rebuild resource pack after reload: " + error.getMessage()));
    }
}
