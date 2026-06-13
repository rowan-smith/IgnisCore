package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.port.IgnisInteraction;

import java.util.Locale;
import java.util.Map;

/**
 * Resolves configured interaction actions from extension YAML {@code interactions} sections.
 * Item configs typically declare {@code left_click}/{@code right_click} actions here; block
 * configs use the same keys and may also declare material-specific overrides.
 */
public final class InteractionSettings {
    private InteractionSettings() {
    }

    /**
     * Returns the configured action name for an item interaction, or an empty string when none is set.
     * Examples: {@code throw}, {@code assign_bomb}, {@code detonate_linked}.
     */
    public static String itemAction(Map<String, Object> interactionSettings, IgnisInteraction interaction) {
        String clickKey = clickKey(interaction);
        if (clickKey.isEmpty()) {
            return "";
        }
        return ExtensionConfig.of(interactionSettings)
                .section(clickKey)
                .getString("action", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Returns true when the given interaction has a non-empty configured action.
     */
    public static boolean handles(Map<String, Object> interactionSettings, IgnisInteraction interaction) {
        return !itemAction(interactionSettings, interaction).isEmpty();
    }

    private static String clickKey(IgnisInteraction interaction) {
        return switch (interaction) {
            case LEFT_CLICK_BLOCK, LEFT_CLICK_AIR -> "left_click";
            case RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR -> "right_click";
            case PHYSICAL -> "physical";
        };
    }
}
