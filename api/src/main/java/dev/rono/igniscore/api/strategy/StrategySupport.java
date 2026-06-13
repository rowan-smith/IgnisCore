package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.Map;

/**
 * Pure data helpers for strategy implementations. World mutations use {@link IgnisWorld}.
 */
public final class StrategySupport {
    private StrategySupport() {
    }

    public static int fuse(BlockDefinition definition, int defaultFuse) {
        return customInt(definition.getCustomData(), "fuse", defaultFuse);
    }

    public static double radius(BlockDefinition definition, double defaultRadius) {
        return customDouble(definition.getCustomData(), "radius", defaultRadius);
    }

    public static float resolvePower(BlockDefinition definition, double defaultPower) {
        return resolvePower(definition.getCustomData(), defaultPower);
    }

    public static float resolvePower(ItemDefinition definition, double defaultPower) {
        return resolvePower(definition.getCustomData(), defaultPower);
    }

    public static float resolvePower(Map<String, Object> customData, double defaultPower) {
        double base = customDouble(customData, "radius", 0);
        if (base <= 0) {
            base = customDouble(customData, "power", defaultPower);
        }
        return (float) (base * customDouble(customData, "multiplier", 1.0));
    }

    public static void createExplosion(IgnisWorld world, IgnisLocation location, BlockDefinition definition,
                                       double defaultPower, boolean defaultFire) {
        createExplosion(world, location, definition.getCustomData(), defaultPower, defaultFire);
    }

    public static void createExplosion(IgnisWorld world, IgnisLocation location, ItemDefinition definition,
                                       double defaultPower, boolean defaultFire) {
        createExplosion(world, location, definition.getCustomData(), defaultPower, defaultFire);
    }

    public static void createExplosion(IgnisWorld world, IgnisLocation location, Map<String, Object> customData,
                                       double defaultPower, boolean defaultFire) {
        world.createExplosion(
                location,
                resolvePower(customData, defaultPower),
                customBoolean(customData, "fire", defaultFire),
                customBoolean(customData, "blockDamage", true)
        );
    }

    public static void createExplosion(IgnisWorld world, IgnisLocation location, float power, boolean fire, boolean blockDamage) {
        world.createExplosion(location, power, fire, blockDamage);
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

    public static double customDouble(Map<String, Object> customData, String key, double defaultValue) {
        return ExtensionConfig.of(customData).getDouble(key, defaultValue);
    }

    public static int customInt(Map<String, Object> customData, String key, int defaultValue) {
        return ExtensionConfig.of(customData).getInt(key, defaultValue);
    }

    public static boolean customBoolean(Map<String, Object> customData, String key, boolean defaultValue) {
        return ExtensionConfig.of(customData).getBoolean(key, defaultValue);
    }

    public static int fuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
        return Math.max(1, fuse(instance.getDefinition(), defaultFuse));
    }

    public static int elapsedFuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
        int fuse = fuseTicks(instance, defaultFuse);
        return Math.max(0, fuse - instance.getTicksLeft());
    }
}
