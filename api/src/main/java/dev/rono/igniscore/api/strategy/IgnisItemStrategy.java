package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Runtime behavior for custom item types.
 */
public interface IgnisItemStrategy extends IgnisStrategy {

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisInteraction action) {}

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        onItemUse(player, definition, item, action);
    }
}
