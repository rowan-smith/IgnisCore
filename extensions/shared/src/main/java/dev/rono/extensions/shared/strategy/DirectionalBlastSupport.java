package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

/**
 * Directional blast patterns (slingshot, claymore cone, forward burst).
 */
public final class DirectionalBlastSupport {
    private DirectionalBlastSupport() {
    }

    public static float resolveYaw(IgnisStrategyContext context, Object triggerContext, IgnisLocation fallback) {
        IgnisPlayer player = context.extensions().wrapPlayer(triggerContext);
        if (player != null) {
            return player.getEyeLocation().yaw();
        }
        return PlacedMetaSupport.placementYaw(fallback, 0f);
    }

    public static void forwardBlast(IgnisWorld world,
                                     IgnisLocation origin,
                                     float yaw,
                                     int steps,
                                     double stepDistance,
                                     float power,
                                     boolean blockDamage) {
        double yawRad = Math.toRadians(yaw);
        double dirX = -Math.sin(yawRad);
        double dirZ = Math.cos(yawRad);
        for (int i = 1; i <= steps; i++) {
            IgnisLocation point = origin.add(dirX * stepDistance * i, 0, dirZ * stepDistance * i);
            ExplosionSupport.createExplosion(world, point, power, false, blockDamage);
            world.spawnParticle(point, "FLAME", 8, 0.3, 0.2, 0.3, 0.02);
        }
    }

    public static void coneBlast(IgnisWorld world,
                                IgnisLocation origin,
                                float yaw,
                                double range,
                                double coneAngle,
                                double knockbackStrength) {
        EntityPhysicsSupport.coneKnockback(world, origin, yaw, coneAngle, range, knockbackStrength);
        world.spawnParticle(origin, "SWEEP_ATTACK", 3, 0.2, 0.2, 0.2, 0.01);
        world.playSound(origin, "ENTITY_PLAYER_ATTACK_SWEEP", 1.5f, 0.6f);
        ExplosionSupport.createExplosion(world, origin, 2.5f, false, false);
    }
}
