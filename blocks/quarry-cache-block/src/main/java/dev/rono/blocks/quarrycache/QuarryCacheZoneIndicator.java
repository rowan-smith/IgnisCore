package dev.rono.blocks.quarrycache;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

final class QuarryCacheZoneIndicator {
    private static final Particle.DustOptions DUST = new Particle.DustOptions(Color.fromRGB(255, 170, 0), 0.8f);

    private QuarryCacheZoneIndicator() {
    }

    static void spawn(QuarryCacheData cache) {
        Location center = cache.center();
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        double radius = cache.collectRadius;
        double depth = cache.collectDepth;
        double centerX = center.getX() + 0.5;
        double centerY = center.getY() + 0.5;
        double centerZ = center.getZ() + 0.5;
        int circlePoints = Math.max(16, (int) Math.ceil(radius * 10));

        drawCircle(world, centerX, centerY - depth, centerZ, radius, circlePoints);
        drawCircle(world, centerX, centerY, centerZ, radius, circlePoints);
        drawCircle(world, centerX, centerY + depth, centerZ, radius, circlePoints);

        for (int pillar = 0; pillar < 4; pillar++) {
            double angle = pillar * (Math.PI / 2.0);
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            for (double y = centerY - depth; y <= centerY + depth; y += 0.75) {
                world.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, DUST);
            }
        }
    }

    private static void drawCircle(World world, double centerX, double y, double centerZ, double radius, int points) {
        for (int point = 0; point < points; point++) {
            double angle = (Math.PI * 2.0 * point) / points;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            world.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, DUST);
        }
    }
}
