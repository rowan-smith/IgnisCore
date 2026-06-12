package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.ExtensionSupport;

final class QuarryCacheZoneIndicator {
    private QuarryCacheZoneIndicator() {
    }

    static void spawn(QuarryCacheData cache, ExtensionSupport extensionSupport) {
        IgnisLocation center = cache.center();
        IgnisWorld world = extensionSupport.resolveWorld(center);
        if (world == null) {
            return;
        }

        double radius = cache.collectRadius();
        double depth = cache.collectDepth();
        double centerX = center.x();
        double centerY = center.y();
        double centerZ = center.z();
        int circlePoints = Math.max(16, (int) Math.ceil(radius * 10));

        drawCircle(world, centerX, centerY - depth, centerZ, radius, circlePoints);
        drawCircle(world, centerX, centerY, centerZ, radius, circlePoints);
        drawCircle(world, centerX, centerY + depth, centerZ, radius, circlePoints);

        for (int pillar = 0; pillar < 4; pillar++) {
            double angle = pillar * (Math.PI / 2.0);
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            for (double y = centerY - depth; y <= centerY + depth; y += 0.75) {
                world.spawnParticle(
                        new IgnisLocation(center.worldId(), center.worldName(), x, y, z, 0f, 0f),
                        "END_ROD", 1, 0, 0, 0, 0);
            }
        }
    }

    private static void drawCircle(IgnisWorld world, double centerX, double y, double centerZ, double radius, int points) {
        for (int point = 0; point < points; point++) {
            double angle = (Math.PI * 2.0 * point) / points;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            world.spawnParticle(new IgnisLocation(null, world.getName(), x, y, z, 0f, 0f), "END_ROD", 1, 0, 0, 0, 0);
        }
    }
}
