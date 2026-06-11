package dev.rono.igniscore.api;

import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.ItemDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

/**
 * Public runtime facade implemented by the core plugin.
 */
public interface IgnisCoreFacade {
    Map<String, BlockDefinition> getBlockTypes();

    Map<String, ItemDefinition> getItemTypes();

    RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context);

    String getPlacedBlockType(Location location);

    Collection<RuntimeBlockInstance> getActiveBlocks();

    ItemStack createBlockItem(String typeId);

    ItemStack createItem(String typeId);

    IgnisStrategyRegistry getStrategyRegistry();

    IgnisNbtService getNbtService();

    void reloadExtensions();
}
