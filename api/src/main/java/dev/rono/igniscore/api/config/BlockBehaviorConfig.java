package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;
import dev.rono.igniscore.api.strategy.StrategyProfile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Typed view of the standard {@code behavior} YAML section for blocks.
 *
 * <p>Controls surface click routing, combustibility, ignition materials, and ignite sounds or
 * effects. Strategy defaults from {@link StrategyProfile} apply when a behavior key is omitted.</p>
 */
public final class BlockBehaviorConfig {
    private final Boolean combustible;
    private final CustomBlockAction leftClickBlock;
    private final CustomBlockAction rightClickBlock;
    private final CustomBlockAction leftClickAir;
    private final CustomBlockAction rightClickAir;
    private final List<String> ignitionMaterials;
    private final String placementSound;
    private final String igniteSound;
    private final ExtensionConfig igniteEffects;

    private BlockBehaviorConfig(Boolean combustible,
                                CustomBlockAction leftClickBlock,
                                CustomBlockAction rightClickBlock,
                                CustomBlockAction leftClickAir,
                                CustomBlockAction rightClickAir,
                                List<String> ignitionMaterials,
                                String placementSound,
                                String igniteSound,
                                ExtensionConfig igniteEffects) {
        this.combustible = combustible;
        this.leftClickBlock = leftClickBlock;
        this.rightClickBlock = rightClickBlock;
        this.leftClickAir = leftClickAir;
        this.rightClickAir = rightClickAir;
        this.ignitionMaterials = ignitionMaterials == null ? List.of() : List.copyOf(ignitionMaterials);
        this.placementSound = placementSound;
        this.igniteSound = igniteSound;
        this.igniteEffects = igniteEffects == null ? ExtensionConfig.empty() : igniteEffects;
    }

    /**
     * @return config with no behavior overrides
     */
    public static BlockBehaviorConfig empty() {
        return new BlockBehaviorConfig(null, null, null, null, null, List.of(), null, null, ExtensionConfig.empty());
    }

    /**
     * Parses the {@code behavior} section from an extension config map.
     *
     * @param config behavior section wrapped as {@link ExtensionConfig}
     * @return parsed behavior settings, or {@link #empty()} when absent
     */
    public static BlockBehaviorConfig from(ExtensionConfig config) {
        if (config == null || config.asMap().isEmpty()) {
            return empty();
        }

        ExtensionConfig sounds = config.section("sounds");
        ExtensionConfig effects = config.section("effects");

        return new BlockBehaviorConfig(
                config.contains("combustible") ? config.getBoolean("combustible", false) : null,
                parseAction(config.getString("left_click_block", null)),
                parseAction(config.getString("right_click_block", null)),
                parseAction(config.getString("left_click_air", null)),
                parseAction(config.getString("right_click_air", null)),
                YamlDefinitions.stringList(config.asMap(), "ignition_materials"),
                sounds.getString("place", null),
                sounds.getString("ignite", null),
                effects.section("ignite"));
    }

    /**
     * @return {@code true} when no behavior keys were configured
     */
    public boolean isEmpty() {
        return combustible == null
                && leftClickBlock == null
                && rightClickBlock == null
                && leftClickAir == null
                && rightClickAir == null
                && ignitionMaterials.isEmpty()
                && placementSound == null
                && igniteSound == null
                && igniteEffects.asMap().isEmpty();
    }

    /**
     * Overlays configured behavior onto a base strategy profile.
     *
     * @param base strategy defaults from the extension
     * @return merged profile, or {@code base} unchanged when {@link #isEmpty()}
     */
    public StrategyProfile merge(StrategyProfile base) {
        if (isEmpty()) {
            return base;
        }

        StrategyProfile.Builder builder = base.toBuilder();
        if (combustible != null) {
            builder.combustible(combustible);
        }
        if (leftClickBlock != null) {
            builder.leftClickAction(leftClickBlock);
        }
        if (rightClickBlock != null) {
            builder.rightClickAction(rightClickBlock);
        }
        if (!ignitionMaterials.isEmpty()) {
            builder.ignitionMaterials(ignitionMaterials);
        }
        if (placementSound != null) {
            builder.placementSound(placementSound);
        }
        if (igniteSound != null) {
            builder.igniteSound(igniteSound);
        }
        return builder.build();
    }

    /**
     * Resolves the click action for a player interaction, applying ignition rules for
     * right-click block when combustible.
     *
     * @param interaction player interaction type
     * @param profile merged strategy profile
     * @param materialKey held item material key for ignition checks
     * @return resolved block action
     */
    public CustomBlockAction resolve(IgnisInteraction interaction, StrategyProfile profile, String materialKey) {
        return switch (interaction) {
            case LEFT_CLICK_BLOCK -> orDefault(leftClickBlock, profile.getLeftClickAction());
            case RIGHT_CLICK_BLOCK -> resolveRightClickBlock(profile, materialKey);
            case LEFT_CLICK_AIR -> orDefault(leftClickAir, CustomBlockAction.NONE);
            case RIGHT_CLICK_AIR -> orDefault(rightClickAir, CustomBlockAction.NONE);
            default -> CustomBlockAction.NONE;
        };
    }

    /**
     * @param fallback sound id when {@code sounds.ignite} is not set
     * @return configured ignite sound or the fallback
     */
    public String igniteSoundOr(String fallback) {
        return igniteSound != null ? igniteSound : fallback;
    }

    /**
     * @return nested {@code effects.ignite} section for particle or protocol effects
     */
    public ExtensionConfig igniteEffects() {
        return igniteEffects;
    }

    private CustomBlockAction resolveRightClickBlock(StrategyProfile profile, String materialKey) {
        if (profile.isCombustible() && PlacedClickSupport.isIgnitionMaterial(profile, materialKey)) {
            return CustomBlockAction.IGNITE;
        }
        return orDefault(rightClickBlock, profile.getRightClickAction());
    }

    private static CustomBlockAction orDefault(CustomBlockAction configured, CustomBlockAction fallback) {
        return configured != null ? configured : fallback;
    }

    private static CustomBlockAction parseAction(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "none" -> CustomBlockAction.NONE;
            case "break" -> CustomBlockAction.BREAK;
            case "ignite" -> CustomBlockAction.IGNITE;
            case "open" -> CustomBlockAction.OPEN;
            case "handled" -> CustomBlockAction.HANDLED;
            default -> throw new IllegalArgumentException("Unknown block behavior action: " + raw);
        };
    }
}
