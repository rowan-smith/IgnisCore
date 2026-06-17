package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;

import java.util.List;

/**
 * Default behavior profile for a strategy.
 *
 * <p>Override {@link IgnisBlockStrategy#profile} and {@link IgnisBlockStrategy#onPlacedClick} to
 * declare click behavior, combustibility, fuse defaults, and presentation in code. Profiles are
 * typically merged with YAML {@code behavior} sections before click resolution.</p>
 */
public final class StrategyProfile {
    private final boolean combustible;
    private final boolean placeable;
    private final boolean breakable;
    private final int defaultFuse;
    private final double defaultRadius;
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
        this.defaultFuse = builder.defaultFuse;
        this.defaultRadius = builder.defaultRadius;
        this.leftClickAction = builder.leftClickAction;
        this.rightClickAction = builder.rightClickAction;
        this.ignitionMaterials = List.copyOf(builder.ignitionMaterials);
        this.placementSound = builder.placementSound;
        this.igniteSound = builder.igniteSound;
        this.displayScale = builder.displayScale;
    }

    /**
     * Returns a profile with builder defaults (non-combustible, placeable, breakable, no click actions).
     *
     * @return default profile instance
     */
    public static StrategyProfile defaults() {
        return builder().build();
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
     * @return fuse length in ticks
     */
    public int getDefaultFuse() {
        return defaultFuse;
    }

    /**
     * Returns the default explosion radius when the strategy does not supply an override.
     *
     * @return radius in blocks
     */
    public double getDefaultRadius() {
        return defaultRadius;
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
        return new Builder()
                .combustible(combustible)
                .placeable(placeable)
                .breakable(breakable)
                .defaultFuse(defaultFuse)
                .defaultRadius(defaultRadius)
                .leftClickAction(leftClickAction)
                .rightClickAction(rightClickAction)
                .ignitionMaterials(ignitionMaterials)
                .placementSound(placementSound)
                .igniteSound(igniteSound)
                .displayScale(displayScale);
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
        /** Default fuse duration in ticks. Default: {@code 0}. */
        private int defaultFuse = 0;
        /** Default explosion radius in blocks. Default: {@code 0.0}. */
        private double defaultRadius = 0.0;
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
         * Sets the default fuse duration in ticks.
         *
         * @param defaultFuse fuse length in ticks
         * @return this builder
         */
        public Builder defaultFuse(int defaultFuse) {
            this.defaultFuse = defaultFuse;
            return this;
        }

        /**
         * Sets the default explosion radius in blocks.
         *
         * @param defaultRadius radius in blocks
         * @return this builder
         */
        public Builder defaultRadius(double defaultRadius) {
            this.defaultRadius = defaultRadius;
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
