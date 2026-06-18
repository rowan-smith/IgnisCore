package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

import java.util.Map;
import java.util.Optional;

/**
 * Grouped entry point for block and item strategy helpers in the core API.
 *
 * <p>Prefer {@link #blocks()}, {@link #items()}, and {@link #data()} over importing individual
 * support classes when authoring extensions. Optional shared helpers live in
 * {@link dev.rono.extensions.shared.ExtensionShared}.</p>
 */
public final class IgnisStrategies {
    private IgnisStrategies() {
    }

    /** Block behavior config and click routing. */
    public static Blocks blocks() {
        return Blocks.INSTANCE;
    }

    /** Item behavior token resolution. */
    public static Items items() {
        return Items.INSTANCE;
    }

    /** Typed reads from {@code custom_data} maps. */
    public static Data data() {
        return Data.INSTANCE;
    }

    public static final class Blocks {
        static final Blocks INSTANCE = new Blocks();

        private Blocks() {
        }

        public BlockBehaviorConfig behavior(BlockDefinition definition) {
            return BlockBehaviorConfig.from(definition.getBehaviorConfig());
        }

        public CustomBlockAction click(BlockDefinition definition,
                                       CustomBlockAction left,
                                       CustomBlockAction right,
                                       IgnisInteraction interaction,
                                       IgnisItem heldItem) {
            return PlacedClickSupport.resolve(definition, left, right, interaction, heldItem);
        }

        public CustomBlockAction click(BlockDefinition definition,
                                       CustomBlockAction left,
                                       CustomBlockAction right,
                                       IgnisInteraction interaction,
                                       String materialKey) {
            return PlacedClickSupport.resolve(definition, left, right, interaction, materialKey);
        }

        public boolean isIgnitionMaterial(BlockDefinition definition, String materialKey) {
            return PlacedClickSupport.isIgnitionMaterial(definition, materialKey);
        }
    }

    public static final class Items {
        static final Items INSTANCE = new Items();

        private Items() {
        }

        public Optional<String> actionFor(ItemDefinition definition, IgnisInteraction interaction) {
            return ItemUseSupport.actionFor(definition, interaction);
        }

        public boolean triggers(ItemDefinition definition, IgnisInteraction interaction) {
            return ItemUseSupport.triggers(definition, interaction);
        }
    }

    public static final class Data {
        static final Data INSTANCE = new Data();

        private Data() {
        }

        public int customInt(BlockDefinition definition, String key, int defaultValue) {
            return StrategySupport.customInt(definition, key, defaultValue);
        }

        public double customDouble(BlockDefinition definition, String key, double defaultValue) {
            return StrategySupport.customDouble(definition, key, defaultValue);
        }

        public boolean customBoolean(BlockDefinition definition, String key, boolean defaultValue) {
            return StrategySupport.customBoolean(definition, key, defaultValue);
        }

        public String customString(BlockDefinition definition, String key, String defaultValue) {
            return StrategySupport.customString(definition, key, defaultValue);
        }

        public int customInt(ItemDefinition definition, String key, int defaultValue) {
            return StrategySupport.customInt(definition, key, defaultValue);
        }

        public double customDouble(ItemDefinition definition, String key, double defaultValue) {
            return StrategySupport.customDouble(definition, key, defaultValue);
        }

        public boolean customBoolean(ItemDefinition definition, String key, boolean defaultValue) {
            return StrategySupport.customBoolean(definition, key, defaultValue);
        }

        public String customString(ItemDefinition definition, String key, String defaultValue) {
            return StrategySupport.customString(definition, key, defaultValue);
        }

        public int customInt(Map<String, Object> customData, String key, int defaultValue) {
            return StrategySupport.customInt(customData, key, defaultValue);
        }

        public double customDouble(Map<String, Object> customData, String key, double defaultValue) {
            return StrategySupport.customDouble(customData, key, defaultValue);
        }

        public boolean customBoolean(Map<String, Object> customData, String key, boolean defaultValue) {
            return StrategySupport.customBoolean(customData, key, defaultValue);
        }

        public String customString(Map<String, Object> customData, String key, String defaultValue) {
            return StrategySupport.customString(customData, key, defaultValue);
        }
    }
}
