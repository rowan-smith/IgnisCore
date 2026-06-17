package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;

import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Default behavior profile for a block strategy.
 *
 * <p>Override {@link IgnisBlockStrategy#profile} to declare click behavior, combustibility, fuse
 * timing, and presentation. Fuse and explosion radius are <em>opt-in</em> — use {@link #fuse(int)}
 * or {@link #combustible(int, double)} for fuse blocks, and {@link #placed()} for placed utility
 * and interact blocks that do not need a fuse lifecycle.</p>
 *
 * <p>Profiles are typically merged with YAML {@code behavior} sections before click resolution.</p>
 */
public final class StrategyProfile {
    private final boolean combustible;
    private final boolean placeable;
    private final boolean breakable;
    private final OptionalInt fuseTicks;
    private final OptionalDouble explosionRadius;
    private final CustomBlockAction leftClickAction;
    private final CustomBlockAction rightClickAction;
    private final List<String> ignitionMaterials;
    private final String placementSound;
    private final String igniteSound;
    private final double displayScale;

    private StrategyProfile(Builder builder) {
        this.combustible = builder.combustible;
        this.placeable = builder.placeable;
        this.breakable = builder.breakable;
        this.fuseTicks = builder.fuseTicks;
        this.explosionRadius = builder.explosionRadius;
        this.leftClickAction = builder.leftClickAction;
        this.rightClickAction = builder.rightClickAction;
        this.ignitionMaterials = List.copyOf(builder.ignitionMaterials);
        this.placementSound = builder.placementSound;
        this.igniteSound = builder.igniteSound;
        this.displayScale = builder.displayScale;
    }

    /**
     * Returns a neutral profile for placed utility, interact, and GUI blocks.
     *
     * <p>Non-combustible, no fuse lifecycle, no click actions.</p>
     *
     * @return placed-block profile
     */
    public static StrategyProfile placed() {
        return builder().build();
    }

    /**
     * Returns a profile for fuse lifecycle blocks ({@code onTick} / {@code onTrigger}).
     *
     * <p>Does not enable combustible ignition — set {@code behavior.combustible: true} in YAML or
     * chain {@link Builder#combustible(boolean)} when the block is ignited with flint and steel.</p>
     *
     * @param fuseTicks fuse duration in ticks ({@code 0} for instant trigger)
     * @return fuse profile
     */
    public static StrategyProfile fuse(int fuseTicks) {
        return builder().defaultFuse(fuseTicks).build();
    }

    /**
     * Returns a profile for combustible explosive blocks with standard click routing.
     *
     * @param fuseTicks fuse duration in ticks
     * @param radius default explosion radius in blocks
     * @return combustible explosive profile
     */
    public static StrategyProfile combustible(int fuseTicks, double radius) {
        return builder()
                .combustible(true)
                .defaultFuse(fuseTicks)
                .defaultRadius(radius)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.IGNITE)
                .ignitionMaterials(List.of("FLINT_AND_STEEL", "FIRE_CHARGE"))
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .igniteSound("ITEM_FLINTANDSTEEL_USE")
                .displayScale(1.01)
                .build();
    }

    /**
     * Creates a new builder for constructing a profile.
     *
     * @return builder initialized with default field values
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns whether this profile declares a fuse lifecycle ({@code onTick} / {@code onTrigger}).
     *
     * @return {@code true} when {@link #defaultFuse(int)} was set on the builder or via {@link #fuse(int)}
     */
    public boolean hasFuseLifecycle() {
        return fuseTicks.isPresent();
    }

    /**
     * Returns whether this profile declares a default explosion radius.
     *
     * @return {@code true} when {@link #defaultRadius(double)} was set on the builder
     */
    public boolean hasExplosionRadius() {
        return explosionRadius.isPresent();
    }

    /**
     * Returns whether right-clicking with an ignition material should start the fuse lifecycle.
     *
     * @return {@code true} when combustible ignition is enabled
     */
    public boolean isCombustible() {
        return combustible;
    }

    /**
     * Returns whether the block type may be placed from its item form.
     *
     * @return {@code true} when placement is allowed
     */
    public boolean isPlaceable() {
        return placeable;
    }

    /**
     * Returns whether players may break the placed block through normal mining.
     *
     * @return {@code true} when breaking is allowed
     */
    public boolean isBreakable() {
        return breakable;
    }

    /**
     * Returns the default fuse duration in ticks when ignition does not override it.
     *
     * @return fuse length in ticks, or {@code 0} when no fuse lifecycle was configured
     */
    public int getDefaultFuse() {
        return fuseTicks.orElse(0);
    }

    /**
     * Returns the default explosion radius when the strategy does not supply an override.
     *
     * @return radius in blocks, or {@code 0.0} when no radius was configured
     */
    public double getDefaultRadius() {
        return explosionRadius.orElse(0.0);
    }

    /**
     * Returns the action resolved for left-clicking a placed block.
     *
     * @return left-click {@link CustomBlockAction}
     */
    public CustomBlockAction getLeftClickAction() {
        return leftClickAction;
    }

    /**
     * Returns the action resolved for right-clicking when ignition does not apply.
     *
     * @return right-click {@link CustomBlockAction}
     */
    public CustomBlockAction getRightClickAction() {
        return rightClickAction;
    }

    /**
     * Returns material keys that can ignite this block when {@link #isCombustible()} is {@code true}.
     *
     * @return immutable list of material keys (for example {@code FLINT_AND_STEEL})
     */
    public List<String> getIgnitionMaterials() {
        return ignitionMaterials;
    }

    /**
     * Returns the sound played when the block is placed, or {@code null} for the core default.
     *
     * @return placement sound key, or {@code null}
     */
    public String getPlacementSound() {
        return placementSound;
    }

    /**
     * Returns the sound played when the block is ignited, or {@code null} for the core default.
     *
     * @return ignition sound key, or {@code null}
     */
    public String getIgniteSound() {
        return igniteSound;
    }

    /**
     * Returns the visual scale factor applied to block display entities.
     *
     * @return display scale multiplier
     */
    public double getDisplayScale() {
        return displayScale;
    }

    /**
     * Returns a builder pre-populated with this profile's field values.
     *
     * @return mutable builder copy of this profile
     */
    public Builder toBuilder() {
        Builder builder = new Builder()
                .combustible(combustible)
                .placeable(placeable)
                .breakable(breakable)
                .leftClickAction(leftClickAction)
                .rightClickAction(rightClickAction)
                .ignitionMaterials(ignitionMaterials)
                .placementSound(placementSound)
                .igniteSound(igniteSound)
                .displayScale(displayScale);
        fuseTicks.ifPresent(builder::defaultFuse);
        explosionRadius.ifPresent(builder::defaultRadius);
        return builder;
    }

    /**
     * Mutable builder for {@link StrategyProfile} instances.
     */
    public static final class Builder {
        /** Whether right-click ignition is enabled. Default: {@code false}. */
        private boolean combustible = false;
        /** Whether the block may be placed from an item. Default: {@code true}. */
        private boolean placeable = true;
        /** Whether the placed block may be mined or broken. Default: {@code true}. */
        private boolean breakable = true;
        /** Fuse duration in ticks when configured. Default: unset (no fuse lifecycle). */
        private OptionalInt fuseTicks = OptionalInt.empty();
        /** Explosion radius in blocks when configured. Default: unset. */
        private OptionalDouble explosionRadius = OptionalDouble.empty();
        /** Left-click action for placed blocks. Default: {@link CustomBlockAction#NONE}. */
        private CustomBlockAction leftClickAction = CustomBlockAction.NONE;
        /** Right-click action when ignition does not apply. Default: {@link CustomBlockAction#NONE}. */
        private CustomBlockAction rightClickAction = CustomBlockAction.NONE;
        /** Material keys that ignite combustible blocks. Default: empty list. */
        private List<String> ignitionMaterials = List.of();
        /** Optional placement sound key. Default: {@code null}. */
        private String placementSound;
        /** Optional ignition sound key. Default: {@code null}. */
        private String igniteSound;
        /** Display entity scale multiplier. Default: {@code 1.0}. */
        private double displayScale = 1.0;

        /**
         * Sets whether right-clicking with an ignition material starts the fuse lifecycle.
         *
         * @param combustible {@code true} to enable combustible ignition
         * @return this builder
         */
        public Builder combustible(boolean combustible) {
            this.combustible = combustible;
            return this;
        }

        /**
         * Sets whether the block type may be placed from its item form.
         *
         * @param placeable {@code true} to allow placement
         * @return this builder
         */
        public Builder placeable(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        /**
         * Sets whether players may break the placed block through normal mining.
         *
         * @param breakable {@code true} to allow breaking
         * @return this builder
         */
        public Builder breakable(boolean breakable) {
            this.breakable = breakable;
            return this;
        }

        /**
         * Declares a fuse lifecycle with the given default duration in ticks.
         *
         * @param defaultFuse fuse length in ticks ({@code 0} for instant trigger)
         * @return this builder
         */
        public Builder defaultFuse(int defaultFuse) {
            this.fuseTicks = OptionalInt.of(defaultFuse);
            return this;
        }

        /**
         * Declares a default explosion radius in blocks.
         *
         * @param defaultRadius radius in blocks
         * @return this builder
         */
        public Builder defaultRadius(double defaultRadius) {
            this.explosionRadius = OptionalDouble.of(defaultRadius);
            return this;
        }

        /**
         * Sets the action resolved for left-clicking a placed block.
         *
         * @param leftClickAction left-click action
         * @return this builder
         */
        public Builder leftClickAction(CustomBlockAction leftClickAction) {
            this.leftClickAction = leftClickAction;
            return this;
        }

        /**
         * Sets the action resolved for right-clicking when ignition does not apply.
         *
         * @param rightClickAction right-click action
         * @return this builder
         */
        public Builder rightClickAction(CustomBlockAction rightClickAction) {
            this.rightClickAction = rightClickAction;
            return this;
        }

        /**
         * Sets the material keys that can ignite a combustible block.
         *
         * @param ignitionMaterials list of material keys; {@code null} is treated as empty
         * @return this builder
         */
        public Builder ignitionMaterials(List<String> ignitionMaterials) {
            this.ignitionMaterials = ignitionMaterials;
            return this;
        }

        /**
         * Sets the sound played when the block is placed.
         *
         * @param placementSound sound key, or {@code null} for the core default
         * @return this builder
         */
        public Builder placementSound(String placementSound) {
            this.placementSound = placementSound;
            return this;
        }

        /**
         * Sets the sound played when the block is ignited.
         *
         * @param igniteSound sound key, or {@code null} for the core default
         * @return this builder
         */
        public Builder igniteSound(String igniteSound) {
            this.igniteSound = igniteSound;
            return this;
        }

        /**
         * Sets the visual scale factor for block display entities.
         *
         * @param displayScale scale multiplier
         * @return this builder
         */
        public Builder displayScale(double displayScale) {
            this.displayScale = displayScale;
            return this;
        }

        /**
         * Builds an immutable {@link StrategyProfile} from the current builder state.
         *
         * @return new profile instance
         */
        public StrategyProfile build() {
            return new StrategyProfile(this);
        }
    }
}
