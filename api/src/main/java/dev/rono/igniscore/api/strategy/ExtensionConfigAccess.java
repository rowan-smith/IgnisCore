package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

/**
 * Reads typed values from extension {@code custom_data} sections through a strategy context.
 */
public final class ExtensionConfigAccess {

    public int getInt(BlockDefinition definition, String key, int defaultValue) {
        return config(definition).getInt(key, defaultValue);
    }

    public int getInt(ItemDefinition definition, String key, int defaultValue) {
        return config(definition).getInt(key, defaultValue);
    }

    public double getDouble(BlockDefinition definition, String key, double defaultValue) {
        return config(definition).getDouble(key, defaultValue);
    }

    public double getDouble(ItemDefinition definition, String key, double defaultValue) {
        return config(definition).getDouble(key, defaultValue);
    }

    public boolean getBoolean(BlockDefinition definition, String key, boolean defaultValue) {
        return config(definition).getBoolean(key, defaultValue);
    }

    public boolean getBoolean(ItemDefinition definition, String key, boolean defaultValue) {
        return config(definition).getBoolean(key, defaultValue);
    }

    public String getString(BlockDefinition definition, String key, String defaultValue) {
        return config(definition).getString(key, defaultValue);
    }

    public String getString(ItemDefinition definition, String key, String defaultValue) {
        return config(definition).getString(key, defaultValue);
    }

    private static ExtensionConfig config(BlockDefinition definition) {
        return definition.getCustomConfig();
    }

    private static ExtensionConfig config(ItemDefinition definition) {
        return definition.getCustomConfig();
    }
}
