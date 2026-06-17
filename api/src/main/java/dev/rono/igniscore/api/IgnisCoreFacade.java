package dev.rono.igniscore.api;

import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

import java.util.Collection;
import java.util.Map;

/**
 * Public runtime facade implemented by the core runtime.
 */
public interface IgnisCoreFacade {
    Map<String, BlockDefinition> getBlockTypes();

    Map<String, ItemDefinition> getItemTypes();

    RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context);

    RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context);

    String getPlacedBlockType(IgnisLocation location);

    Collection<RuntimeBlockInstance> getActiveBlocks();

    IgnisItem createBlockItem(String typeId);

    IgnisItem createItem(String typeId);

    IgnisStrategyRegistry getStrategyRegistry();

    IgnisNbtService getNbtService();

    IgnisProtocolService getProtocolService();

    IgnisEffectService getEffectService();

    IgnisRegionService getRegionService();

    IgnisHologramService getHologramService();

    IgnisNpcService getNpcService();

    IgnisIntegrationRegistry getIntegrationRegistry();

    void reloadExtensions();
}
