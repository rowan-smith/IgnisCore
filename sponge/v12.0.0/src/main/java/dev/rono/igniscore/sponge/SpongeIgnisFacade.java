package dev.rono.igniscore.sponge;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.core.SpongeExtensionBootstrap;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.loader.SpongeBlockExtensionLoader;
import dev.rono.igniscore.sponge.loader.SpongeItemExtensionLoader;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;
import dev.rono.igniscore.sponge.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.service.SpongeItemManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SpongeIgnisFacade implements dev.rono.igniscore.api.IgnisCoreFacade {
    private final SpongeBlockManager blockManager;
    private final SpongeItemManager itemManager;
    private final SpongeItemFactory itemFactory;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final SpongeExtensionBootstrap extensionBootstrap;

    @Inject
    public SpongeIgnisFacade(SpongeBlockManager blockManager,
                             SpongeItemManager itemManager,
                             SpongeItemFactory itemFactory,
                             IgnisStrategyRegistry strategyRegistry,
                             IgnisNbtService nbtService,
                             IgnisProtocolService protocolService,
                             IgnisEffectService effectService,
                             SpongeExtensionBootstrap extensionBootstrap) {
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.itemFactory = itemFactory;
        this.strategyRegistry = strategyRegistry;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionBootstrap = extensionBootstrap;
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
        return SpongeBridge.wrap(itemFactory.createItem(typeId));
    }

    @Override
    public IgnisItem createItem(String typeId) {
        return SpongeBridge.wrap(itemFactory.createItem(typeId));
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
    public void reloadExtensions() {
        extensionBootstrap.reloadAll();
    }
}
