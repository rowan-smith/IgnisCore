package dev.rono.extensions.shared.config;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.ExplosionSupport;

import java.util.Map;

/**
 * Typed view of common explosive block settings from {@code custom_data} YAML.
 */
public record ExplosionConfig(
        int fuse,
        double radius,
        double power,
        double multiplier,
        boolean fire,
        boolean blockDamage,
        boolean screenShake) {

    public static final int DEFAULT_FUSE = 80;
    public static final double DEFAULT_RADIUS = 4.0;
    public static final double DEFAULT_POWER = 4.0;

    public static ExplosionConfig from(BlockDefinition definition) {
        return from(definition.getCustomConfig());
    }

    public static ExplosionConfig from(ExtensionConfig config) {
        return new ExplosionConfig(
                config.getInt("fuse", DEFAULT_FUSE),
                config.getDouble("radius", DEFAULT_RADIUS),
                config.getDouble("power", DEFAULT_POWER),
                config.getDouble("multiplier", 1.0),
                config.getBoolean("fire", false),
                config.getBoolean("blockDamage", true),
                config.getBoolean("screenShake", false));
    }

    public float resolvedPower() {
        return ExplosionSupport.resolvePower(asMap(), DEFAULT_POWER);
    }

    public Map<String, Object> asMap() {
        return Map.of(
                "fuse", fuse,
                "radius", radius,
                "power", power,
                "multiplier", multiplier,
                "fire", fire,
                "blockDamage", blockDamage,
                "screenShake", screenShake);
    }
}
