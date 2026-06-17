package dev.rono.extensions.shared.config;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.ItemDefinition;

/**
 * Typed view of throwable item settings from {@code custom_data} YAML.
 */
public record ThrowableItemConfig(
        double throwVelocity,
        int fuseTicks,
        double power,
        boolean fire) {

    public static final double DEFAULT_THROW_VELOCITY = 1.2;
    public static final int DEFAULT_FUSE_TICKS = 40;
    public static final double DEFAULT_POWER = 4.0;

    public static ThrowableItemConfig from(ItemDefinition definition) {
        return from(definition.getCustomConfig());
    }

    public static ThrowableItemConfig from(ExtensionConfig config) {
        return new ThrowableItemConfig(
                config.getDouble("throw_velocity", DEFAULT_THROW_VELOCITY),
                config.getInt("fuse_ticks", DEFAULT_FUSE_TICKS),
                config.getDouble("power", DEFAULT_POWER),
                config.getBoolean("fire", false));
    }
}
