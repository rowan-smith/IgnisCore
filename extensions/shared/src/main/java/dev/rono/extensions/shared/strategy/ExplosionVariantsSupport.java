package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

/**
 * Specialized explosion patterns for utility charges.
 */
public final class ExplosionVariantsSupport {
    private ExplosionVariantsSupport() {
    }

    public static void cardinalSplit(IgnisWorld world, IgnisLocation center, float power, double offset) {
        world.playSound(center, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.0f);
        world.createExplosion(center, power * 0.6f, false, false);
        world.createExplosion(center.add(offset, 0, 0), power * 0.45f, false, true);
        world.createExplosion(center.add(-offset, 0, 0), power * 0.45f, false, true);
        world.createExplosion(center.add(0, 0, offset), power * 0.45f, false, true);
        world.createExplosion(center.add(0, 0, -offset), power * 0.45f, false, true);
        TheatricsSupport.pulseRing(world, center, offset, "EXPLOSION");
    }

    public static void ricochetRay(IgnisWorld world, IgnisLocation start, float yaw, int bounces,
                                    double step, float power) {
        double yawRad = Math.toRadians(yaw);
        double dirX = -Math.sin(yawRad);
        double dirZ = Math.cos(yawRad);
        IgnisLocation current = start;
        for (int i = 0; i < bounces; i++) {
            current = current.add(dirX * step, 0, dirZ * step);
            world.createExplosion(current, power, false, true);
            world.spawnParticle(current, "CRIT", 8, 0.3, 0.2, 0.3, 0.05);
            world.playSound(current, "ENTITY_FIREWORK_ROCKET_BLAST", 0.8f, 0.7f + i * 0.1f);
        }
    }

    public static void mirrorBlast(IgnisWorld world, IgnisLocation center, float power, double mirrorY) {
        world.createExplosion(center, power, false, true);
        double dy = (mirrorY * 2) - center.y();
        IgnisLocation mirror = center.add(0, dy - center.y(), 0);
        world.createExplosion(mirror, power * 0.85f, false, true);
        world.spawnParticle(mirror, "END_ROD", 12, 0.4, 0.4, 0.4, 0.02);
    }

    public static void phaseBurst(IgnisWorld world, IgnisLocation center, float power, double radius) {
        world.createExplosion(center, power, false, false);
        EntityUtilSupport.freezeInRadius(world, center, radius);
        TheatricsSupport.sparkle(world, center, "SOUL_FIRE_FLAME", 20);
    }

    public static float resolveYaw(IgnisWorld world, IgnisLocation block, Object triggerContext,
                                    dev.rono.igniscore.api.strategy.IgnisStrategyContext context) {
        var player = context.extensions().wrapPlayer(triggerContext);
        if (player != null) {
            return player.getEyeLocation().yaw();
        }
        return PlacedMetaSupport.placementYaw(block, 0f);
    }
}
