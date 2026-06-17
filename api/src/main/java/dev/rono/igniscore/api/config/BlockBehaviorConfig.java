package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.strategy.StrategyProfile;

import java.util.List;
import java.util.Map;

/**
 * Typed view of the standard {@code behavior} YAML section for blocks.
 *
 * <p>Controls combustibility, ignition materials, and placement/ignite sounds or effects.
 * Click routing is handled by extension {@code OnBlockClickListener} subscriptions.</p>
 */
public final class BlockBehaviorConfig {
    private final Boolean combustible;
    private final List<String> ignitionMaterials;
    private final String placementSound;
    private final String igniteSound;
    private final ExtensionConfig igniteEffects;

    private BlockBehaviorConfig(Boolean combustible,
                                List<String> ignitionMaterials,
                                String placementSound,
                                String igniteSound,
                                ExtensionConfig igniteEffects) {
        this.combustible = combustible;
        this.ignitionMaterials = ignitionMaterials == null ? List.of() : List.copyOf(ignitionMaterials);
        this.placementSound = placementSound;
        this.igniteSound = igniteSound;
        this.igniteEffects = igniteEffects == null ? ExtensionConfig.empty() : igniteEffects;
    }

    /**
     * @return config with no behavior overrides
     */
    public static BlockBehaviorConfig empty() {
        return new BlockBehaviorConfig(null, List.of(), null, null, ExtensionConfig.empty());
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
}
