package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.Map;

/**
 * Pure data helpers for reading custom configuration values inside strategy implementations.
 *
 * <p>Values are sourced from a block definition's {@code custom} YAML section or from a raw
 * custom-data map. Missing keys return the supplied default without throwing.</p>
 */
public final class StrategySupport {
    private StrategySupport() {
    }

    /**
     * Reads a double from a block definition's custom config section.
     *
     * @param definition block definition carrying custom data
     * @param key config key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed double or {@code defaultValue}
     */
    public static double customDouble(BlockDefinition definition, String key, double defaultValue) {
        return customDouble(definition.getCustomData(), key, defaultValue);
    }

    /**
     * Reads a boolean from a block definition's custom config section.
     *
     * @param definition block definition carrying custom data
     * @param key config key
     * @param defaultValue value when the key is absent or not boolean
     * @return parsed boolean or {@code defaultValue}
     */
    public static boolean customBoolean(BlockDefinition definition, String key, boolean defaultValue) {
        return customBoolean(definition.getCustomData(), key, defaultValue);
    }

    /**
     * Reads an int from a block definition's custom config section.
     *
     * @param definition block definition carrying custom data
     * @param key config key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed int or {@code defaultValue}
     */
    public static int customInt(BlockDefinition definition, String key, int defaultValue) {
        return customInt(definition.getCustomData(), key, defaultValue);
    }

    /**
     * Reads a string from a block definition's custom config section.
     *
     * @param definition block definition carrying custom data
     * @param key config key
     * @param defaultValue value when the key is absent or not a string
     * @return parsed string or {@code defaultValue}
     */
    public static String customString(BlockDefinition definition, String key, String defaultValue) {
        return customString(definition.getCustomData(), key, defaultValue);
    }

    /**
     * Reads a double from a raw custom-data map.
     *
     * @param customData map of extension-specific config values
     * @param key config key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed double or {@code defaultValue}
     */
    public static double customDouble(Map<String, Object> customData, String key, double defaultValue) {
        return ExtensionConfig.of(customData).getDouble(key, defaultValue);
    }

    /**
     * Reads an int from a raw custom-data map.
     *
     * @param customData map of extension-specific config values
     * @param key config key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed int or {@code defaultValue}
     */
    public static int customInt(Map<String, Object> customData, String key, int defaultValue) {
        return ExtensionConfig.of(customData).getInt(key, defaultValue);
    }

    /**
     * Reads a boolean from a raw custom-data map.
     *
     * @param customData map of extension-specific config values
     * @param key config key
     * @param defaultValue value when the key is absent or not boolean
     * @return parsed boolean or {@code defaultValue}
     */
    public static boolean customBoolean(Map<String, Object> customData, String key, boolean defaultValue) {
        return ExtensionConfig.of(customData).getBoolean(key, defaultValue);
    }

    /**
     * Reads a string from a raw custom-data map.
     *
     * @param customData map of extension-specific config values
     * @param key config key
     * @param defaultValue value when the key is absent or not a string
     * @return parsed string or {@code defaultValue}
     */
    public static String customString(Map<String, Object> customData, String key, String defaultValue) {
        return ExtensionConfig.of(customData).getString(key, defaultValue);
    }
}
