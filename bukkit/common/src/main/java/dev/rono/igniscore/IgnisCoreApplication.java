package dev.rono.igniscore;

import com.google.inject.Inject;
import dev.rono.igniscore.api.IgnisCoreFacade;
import dev.rono.igniscore.core.IgnisCoreFacadeImpl;
import dev.rono.igniscore.core.IgnisRuntimeLifecycle;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;

public class IgnisCoreApplication implements IgnisCoreFacade {
    private final IgnisCoreFacadeImpl facade;
    private final IgnisRuntimeLifecycle lifecycle;
    private final BlockItemFactory blockItemFactory;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final NBTService nbtService;
    private final ProtocolService protocolService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;
    private final ResourcePackService resourcePackService;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;

    @Inject
    public IgnisCoreApplication(IgnisCoreFacadeImpl facade,
                                IgnisRuntimeLifecycle lifecycle,
                                BlockItemFactory blockItemFactory,
                                BlockManager blockManager,
                                ItemManager itemManager,
                                NBTService nbtService,
                                ProtocolService protocolService,
                                RuntimeBlockService runtimeBlockService,
                                VisualEffectService visualEffectService,
                                ResourcePackService resourcePackService,
                                BlockExtensionLoader blockExtensionLoader,
                                ItemExtensionLoader itemExtensionLoader) {
        this.facade = facade;
        this.lifecycle = lifecycle;
        this.blockItemFactory = blockItemFactory;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.runtimeBlockService = runtimeBlockService;
        this.visualEffectService = visualEffectService;
        this.resourcePackService = resourcePackService;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
    }

    public void enable() {
        lifecycle.enable();
    }

    public void disable() {
        lifecycle.disable();
    }

    @Override
    public java.util.Map<String, dev.rono.igniscore.api.model.BlockDefinition> getBlockTypes() {
        return facade.getBlockTypes();
    }

    @Override
    public java.util.Map<String, dev.rono.igniscore.api.model.ItemDefinition> getItemTypes() {
        return facade.getItemTypes();
    }

    @Override
    public dev.rono.igniscore.api.model.RuntimeBlockInstance triggerBlock(
            dev.rono.igniscore.api.port.IgnisLocation location, String typeId, Object context) {
        return facade.triggerBlock(location, typeId, context);
    }

    @Override
    public dev.rono.igniscore.api.model.RuntimeBlockInstance ignitePlacedBlock(
            dev.rono.igniscore.api.port.IgnisLocation location, Object context) {
        return facade.ignitePlacedBlock(location, context);
    }

    @Override
    public String getPlacedBlockType(dev.rono.igniscore.api.port.IgnisLocation location) {
        return facade.getPlacedBlockType(location);
    }

    @Override
    public java.util.Collection<dev.rono.igniscore.api.model.RuntimeBlockInstance> getActiveBlocks() {
        return facade.getActiveBlocks();
    }

    @Override
    public dev.rono.igniscore.api.port.IgnisItem createBlockItem(String typeId) {
        return facade.createBlockItem(typeId);
    }

    @Override
    public dev.rono.igniscore.api.port.IgnisItem createItem(String typeId) {
        return facade.createItem(typeId);
    }

    @Override
    public dev.rono.igniscore.api.strategy.IgnisStrategyRegistry getStrategyRegistry() {
        return facade.getStrategyRegistry();
    }

    @Override
    public dev.rono.igniscore.api.service.IgnisNbtService getNbtService() {
        return facade.nbt();
    }

    @Override
    public dev.rono.igniscore.api.service.IgnisProtocolService getProtocolService() {
        return facade.protocol();
    }

    @Override
    public dev.rono.igniscore.api.service.IgnisEffectService getEffectService() {
        return facade.effects();
    }

    @Override
    public dev.rono.igniscore.api.event.IgnisEventBus eventBus() {
        return facade.eventBus();
    }

    @Override
    public void reloadExtensions() {
        facade.reloadExtensions();
    }

    public BlockItemFactory getBlockItemFactory() {
        return blockItemFactory;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public NBTService getNbtServiceImpl() {
        return nbtService;
    }

    public ProtocolService getProtocolServiceImpl() {
        return protocolService;
    }

    public RuntimeBlockService getRuntimeBlockService() {
        return runtimeBlockService;
    }

    public VisualEffectService getVisualEffectService() {
        return visualEffectService;
    }

    public ResourcePackService getResourcePackService() {
        return resourcePackService;
    }

    public BlockExtensionLoader getBlockExtensionLoader() {
        return blockExtensionLoader;
    }

    public ItemExtensionLoader getItemExtensionLoader() {
        return itemExtensionLoader;
    }
}
