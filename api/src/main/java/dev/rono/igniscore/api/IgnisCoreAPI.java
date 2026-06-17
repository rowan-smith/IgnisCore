package dev.rono.igniscore.api;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

import java.util.Collection;
import java.util.Map;

public final class IgnisCoreAPI {
    private static IgnisCoreFacade facade;

    private IgnisCoreAPI() {
    }

    public static void init(IgnisCoreFacade instance) {
        facade = instance;
    }

    public static Map<String, BlockDefinition> getBlockTypes() {
        return requireFacade().getBlockTypes();
    }

    public static Map<String, ItemDefinition> getItemTypes() {
        return requireFacade().getItemTypes();
    }

    public static RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        return requireFacade().triggerBlock(location, typeId, context);
    }

    public static RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context) {
        return requireFacade().ignitePlacedBlock(location, context);
    }

    public static String getPlacedBlockType(IgnisLocation location) {
        return requireFacade().getPlacedBlockType(location);
    }

    public static Collection<RuntimeBlockInstance> getActiveBlocks() {
        return requireFacade().getActiveBlocks();
    }

    public static IgnisItem createBlockItem(String typeId) {
        return requireFacade().createBlockItem(typeId);
    }

    public static IgnisItem createItem(String typeId) {
        return requireFacade().createItem(typeId);
    }

    public static IgnisStrategyRegistry getStrategyRegistry() {
        return requireFacade().getStrategyRegistry();
    }

    public static IgnisNbtService getNbtService() {
        return requireFacade().getNbtService();
    }

    public static IgnisProtocolService getProtocolService() {
        return requireFacade().getProtocolService();
    }

    public static IgnisEffectService getEffectService() {
        return requireFacade().getEffectService();
    }

    public static IgnisRegionService getRegionService() {
        return requireFacade().getRegionService();
    }

    public static IgnisHologramService getHologramService() {
        return requireFacade().getHologramService();
    }

    public static IgnisNpcService getNpcService() {
        return requireFacade().getNpcService();
    }

    public static IgnisIntegrationRegistry getIntegrationRegistry() {
        return requireFacade().getIntegrationRegistry();
    }

    public static void reloadExtensions() {
        requireFacade().reloadExtensions();
    }

    private static IgnisCoreFacade requireFacade() {
        if (facade == null) {
            throw new IllegalStateException("IgnisCoreAPI has not been initialized");
        }
        return facade;
    }
}
