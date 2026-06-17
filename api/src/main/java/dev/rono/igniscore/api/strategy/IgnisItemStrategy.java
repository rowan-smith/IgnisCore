package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ItemBehaviorConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Runtime behavior for custom item types.
 *
 * <p>The platform cancels vanilla item use and delegates to {@link #onItemUse}. When
 * {@code behavior} is configured in YAML, the default implementation routes action tokens to
 * {@link #onItemAction}. Override {@link #onItemAction} instead of re-parsing
 * {@link ItemBehaviorConfig} in most strategies.</p>
 */
public interface IgnisItemStrategy extends IgnisStrategy {

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisInteraction action) {}

    default void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        ItemBehaviorConfig behavior = ItemBehaviorConfig.from(definition.getBehaviorConfig());
        if (!behavior.isEmpty()) {
            behavior.actionFor(action).ifPresent(token ->
                    onItemAction(player, definition, item, action, clickedBlock, token));
            return;
        }
        onItemUse(player, definition, item, action);
    }

    /**
     * Handles a resolved YAML behavior token for an item interaction.
     *
     * @param actionToken normalized token from {@code behavior} (for example {@code throw}, {@code use})
     */
    default void onItemAction(IgnisPlayer player,
                              ItemDefinition definition,
                              IgnisItem item,
                              IgnisInteraction action,
                              IgnisBlock clickedBlock,
                              String actionToken) {}
}
