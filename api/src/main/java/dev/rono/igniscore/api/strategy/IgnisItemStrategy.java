package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Runtime behavior for custom item types.
 *
 * <p>The platform cancels vanilla item use and delegates to {@link #onItemUse}. Read tuning from
 * {@link ItemDefinition#getCustomConfig()} and declared click actions from
 * {@link ItemDefinition#interactionAction(IgnisInteraction)}.</p>
 *
 * <p>YAML {@code interactions.left_click}/{@code right_click} declare action names (for example
 * {@code throw}, {@code detonate_linked}). Strategies should branch on {@link IgnisInteraction}
 * and/or the configured action string rather than hard-coding item ids.</p>
 */
public interface IgnisItemStrategy extends IgnisStrategy {

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisInteraction action) {}

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        onItemUse(player, definition, item, action);
    }
}
