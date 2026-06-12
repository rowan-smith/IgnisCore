package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Runtime behavior for custom block types.
 * Assign a strategy id in block config via {@code behavior.strategy}.
 */
public interface IgnisBlockStrategy extends IgnisStrategy {

    default StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    default void onStaticPlace(BlockDefinition definition, Location location) {
        onStaticPlace(definition, location, null);
    }

    default void onStaticPlace(BlockDefinition definition, Location location, ItemStack placedFrom) {}

    default void onStaticInteract(BlockDefinition definition, Location location, Player player, CustomBlockAction action) {}

    default void onStaticBreak(BlockDefinition definition, Location location) {
        onStaticBreak(definition, location, null);
    }

    default void onStaticBreak(BlockDefinition definition, Location location, ItemStack droppedItem) {}

    default void onPlace(RuntimeBlockInstance instance) {}

    default void onTick(RuntimeBlockInstance instance) {}

    default void onInteract(RuntimeBlockInstance instance, Player player) {}

    default void onBreak(RuntimeBlockInstance instance) {}

    default void onTrigger(RuntimeBlockInstance instance, Object context) {}
}
