package dev.rono.igniscore.api;

import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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

    public static RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
        return requireFacade().triggerBlock(location, typeId, context);
    }

    public static String getPlacedBlockType(Location location) {
        return requireFacade().getPlacedBlockType(location);
    }

    public static Collection<RuntimeBlockInstance> getActiveBlocks() {
        return requireFacade().getActiveBlocks();
    }

    public static ItemStack createBlockItem(String typeId) {
        return requireFacade().createBlockItem(typeId);
    }

    public static ItemStack createItem(String typeId) {
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
