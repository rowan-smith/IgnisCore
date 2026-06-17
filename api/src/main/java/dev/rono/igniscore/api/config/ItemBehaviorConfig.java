package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.port.IgnisInteraction;

import java.util.Locale;
import java.util.Optional;

/**
 * Typed view of the standard {@code behavior} YAML section for items.
 * Maps player interactions to simple action tokens interpreted by the item strategy.
 */
public final class ItemBehaviorConfig {
    private final String leftClickBlock;
    private final String rightClickBlock;
    private final String leftClickAir;
    private final String rightClickAir;

    private ItemBehaviorConfig(String leftClickBlock,
                               String rightClickBlock,
                               String leftClickAir,
                               String rightClickAir) {
        this.leftClickBlock = normalize(leftClickBlock);
        this.rightClickBlock = normalize(rightClickBlock);
        this.leftClickAir = normalize(leftClickAir);
        this.rightClickAir = normalize(rightClickAir);
    }

    public static ItemBehaviorConfig empty() {
        return new ItemBehaviorConfig(null, null, null, null);
    }

    public static ItemBehaviorConfig from(ExtensionConfig config) {
        if (config == null || config.asMap().isEmpty()) {
            return empty();
        }
        return new ItemBehaviorConfig(
                config.getString("left_click_block", null),
                config.getString("right_click_block", null),
                config.getString("left_click_air", null),
                config.getString("right_click_air", null));
    }

    public boolean isEmpty() {
        return leftClickBlock == null
                && rightClickBlock == null
                && leftClickAir == null
                && rightClickAir == null;
    }

    public Optional<String> actionFor(IgnisInteraction interaction) {
        String action = switch (interaction) {
            case LEFT_CLICK_BLOCK -> leftClickBlock;
            case RIGHT_CLICK_BLOCK -> rightClickBlock;
            case LEFT_CLICK_AIR -> leftClickAir;
            case RIGHT_CLICK_AIR -> rightClickAir;
            default -> null;
        };
        if (action == null || action.isBlank() || "none".equals(action)) {
            return Optional.empty();
        }
        return Optional.of(action);
    }

    public boolean triggers(IgnisInteraction interaction) {
        return actionFor(interaction).isPresent();
    }

    private static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }
}
