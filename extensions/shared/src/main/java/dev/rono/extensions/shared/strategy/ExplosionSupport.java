package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;

import java.util.Map;

/**
 * Explosive behavior helpers for extension strategies. Uses generic {@link StrategySupport}
 * for custom data access.
 */
public final class ExplosionSupport {
    private ExplosionSupport() {
    }

    public static int fuse(BlockDefinition definition, int defaultFuse) {
        return StrategySupport.customInt(definition, "fuse", defaultFuse);
    }

    public static double radius(BlockDefinition definition, double defaultRadius) {
        return StrategySupport.customDouble(definition, "radius", defaultRadius);
    }

    public static float resolvePower(BlockDefinition definition, double defaultPower) {
        return resolvePower(definition.getCustomData(), defaultPower);
    }

    public static float resolvePower(ItemDefinition definition, double defaultPower) {
        return resolvePower(definition.getCustomData(), defaultPower);
    }

    public static float resolvePower(Map<String, Object> customData, double defaultPower) {
        double base = StrategySupport.customDouble(customData, "radius", 0);
        if (base <= 0) {
            base = StrategySupport.customDouble(customData, "power", defaultPower);
        }
        return (float) (base * StrategySupport.customDouble(customData, "multiplier", 1.0));
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
                StrategySupport.customBoolean(customData, "fire", defaultFire),
                StrategySupport.customBoolean(customData, "blockDamage", true)
        );
    }

    public static void createExplosion(IgnisWorld world, IgnisLocation location, float power, boolean fire, boolean blockDamage) {
        world.createExplosion(location, power, fire, blockDamage);
    }

    public static int fuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
        return Math.max(1, fuse(instance.getDefinition(), defaultFuse));
    }

    public static int elapsedFuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
        int fuse = fuseTicks(instance, defaultFuse);
        return Math.max(0, fuse - instance.getTicksLeft());
    }
}
