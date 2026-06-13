package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;

import java.util.List;

/**
 * Default behavior profile for a strategy. Override {@link IgnisBlockStrategy#profile} and
 * {@link IgnisBlockStrategy#onPlacedClick} to declare click behavior in code.
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

    public static StrategyProfile defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isCombustible() {
        return combustible;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public int getDefaultFuse() {
        return defaultFuse;
    }

    public double getDefaultRadius() {
        return defaultRadius;
    }

    public CustomBlockAction getLeftClickAction() {
        return leftClickAction;
    }

    public CustomBlockAction getRightClickAction() {
        return rightClickAction;
    }

    public List<String> getIgnitionMaterials() {
        return ignitionMaterials;
    }

    public String getPlacementSound() {
        return placementSound;
    }

    public String getIgniteSound() {
        return igniteSound;
    }

    public double getDisplayScale() {
        return displayScale;
    }

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

    public static final class Builder {
        private boolean combustible = true;
        private boolean placeable = true;
        private boolean breakable = true;
        private int defaultFuse = 80;
        private double defaultRadius = 4.0;
        private CustomBlockAction leftClickAction = CustomBlockAction.BREAK;
        private CustomBlockAction rightClickAction = CustomBlockAction.IGNITE;
        private List<String> ignitionMaterials = List.of("FLINT_AND_STEEL", "FIRE_CHARGE", "FLINT");
        private String placementSound;
        private String igniteSound = "ITEM_FLINTANDSTEEL_USE";
        private double displayScale = 1.01;

        public Builder combustible(boolean combustible) {
            this.combustible = combustible;
            return this;
        }

        public Builder placeable(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        public Builder breakable(boolean breakable) {
            this.breakable = breakable;
            return this;
        }

        public Builder defaultFuse(int defaultFuse) {
            this.defaultFuse = defaultFuse;
            return this;
        }

        public Builder defaultRadius(double defaultRadius) {
            this.defaultRadius = defaultRadius;
            return this;
        }

        public Builder leftClickAction(CustomBlockAction leftClickAction) {
            this.leftClickAction = leftClickAction;
            return this;
        }

        public Builder rightClickAction(CustomBlockAction rightClickAction) {
            this.rightClickAction = rightClickAction;
            return this;
        }

        public Builder ignitionMaterials(List<String> ignitionMaterials) {
            this.ignitionMaterials = ignitionMaterials;
            return this;
        }

        public Builder placementSound(String placementSound) {
            this.placementSound = placementSound;
            return this;
        }

        public Builder igniteSound(String igniteSound) {
            this.igniteSound = igniteSound;
            return this;
        }

        public Builder displayScale(double displayScale) {
            this.displayScale = displayScale;
            return this;
        }

        public StrategyProfile build() {
            return new StrategyProfile(this);
        }
    }
}
