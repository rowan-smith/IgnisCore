package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ItemBehaviorConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;

import java.util.Optional;

/**
 * Default item interaction resolution from YAML {@code behavior} sections.
 *
 * <p>Extensions implement {@link dev.rono.igniscore.api.event.OnItemClickListener} for custom behavior;
 * use this helper when composing behavior tokens with extension logic outside the default interface routing.</p>
 */
public final class ItemUseSupport {
    private ItemUseSupport() {
    }

    /**
     * Returns the configured behavior action token for a player interaction, if any.
     *
     * @param definition item definition carrying behavior config
     * @param interaction player interaction type
     * @return normalized action token, or empty when unset or {@code none}
     */
    public static Optional<String> actionFor(ItemDefinition definition, IgnisInteraction interaction) {
        return ItemBehaviorConfig.from(definition.getBehaviorConfig()).actionFor(interaction);
    }

    /**
     * Returns {@code true} when the interaction has a configured non-empty action token.
     */
    public static boolean triggers(ItemDefinition definition, IgnisInteraction interaction) {
        return ItemBehaviorConfig.from(definition.getBehaviorConfig()).triggers(interaction);
    }
}
