package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;

/**
 * Particle and sound flourishes for utility extensions.
 */
public final class TheatricsSupport {
    private TheatricsSupport() {
    }

    public static void sparkle(IgnisWorld world, IgnisLocation center, String particle, int count) {
        world.spawnParticle(center, particle, count, 0.35, 0.25, 0.35, 0.02);
    }

    public static void chime(IgnisWorld world, IgnisLocation center, float pitch) {
        world.playSound(center, "BLOCK_NOTE_BLOCK_CHIME", 0.9f, pitch);
    }

    public static void pulseRing(IgnisWorld world, IgnisLocation center, double radius, String particle) {
        for (int i = 0; i < 360; i += 20) {
            double rad = Math.toRadians(i);
            IgnisLocation point = center.add(Math.cos(rad) * radius, 0.1, Math.sin(rad) * radius);
            world.spawnParticle(point, particle, 1, 0, 0, 0, 0);
        }
    }

    public static void scanBeam(IgnisWorld world, IgnisLocation from, IgnisLocation toward, String particle) {
        double dx = toward.x() - from.x();
        double dy = toward.y() - from.y();
        double dz = toward.z() - from.z();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.5) {
            return;
        }
        int steps = (int) Math.min(12, dist);
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            IgnisLocation point = from.add(dx * t, dy * t, dz * t);
            world.spawnParticle(point, particle, 2, 0.05, 0.05, 0.05, 0.01);
        }
    }
}
