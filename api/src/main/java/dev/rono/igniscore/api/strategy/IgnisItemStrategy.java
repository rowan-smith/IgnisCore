package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Runtime behavior for custom item types.
 * Implement this interface in the extension strategy class declared in {@code item-extension.yml}.
 */
public interface IgnisItemStrategy extends IgnisStrategy {

    default void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action) {}

    default void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action, Block clickedBlock) {
        onItemUse(player, definition, item, action);
    }
}
