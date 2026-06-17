package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.Map;

/**
 * Pure data helpers for strategy implementations.
 */
public final class StrategySupport {
    private StrategySupport() {
    }

    public static double customDouble(BlockDefinition definition, String key, double defaultValue) {
        return customDouble(definition.getCustomData(), key, defaultValue);
    }

    public static boolean customBoolean(BlockDefinition definition, String key, boolean defaultValue) {
        return customBoolean(definition.getCustomData(), key, defaultValue);
    }

    public static int customInt(BlockDefinition definition, String key, int defaultValue) {
        return customInt(definition.getCustomData(), key, defaultValue);
    }

    public static String customString(BlockDefinition definition, String key, String defaultValue) {
        return customString(definition.getCustomData(), key, defaultValue);
    }

    public static double customDouble(Map<String, Object> customData, String key, double defaultValue) {
        return ExtensionConfig.of(customData).getDouble(key, defaultValue);
    }

    public static int customInt(Map<String, Object> customData, String key, int defaultValue) {
        return ExtensionConfig.of(customData).getInt(key, defaultValue);
    }

    public static boolean customBoolean(Map<String, Object> customData, String key, boolean defaultValue) {
        return ExtensionConfig.of(customData).getBoolean(key, defaultValue);
    }

    public static String customString(Map<String, Object> customData, String key, String defaultValue) {
        return ExtensionConfig.of(customData).getString(key, defaultValue);
    }
}
