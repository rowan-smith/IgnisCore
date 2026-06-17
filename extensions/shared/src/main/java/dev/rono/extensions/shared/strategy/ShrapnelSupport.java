package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Launches block debris as falling-block projectiles without item drops.
 */
public final class ShrapnelSupport {
    private ShrapnelSupport() {
    }

    public static void launchDebris(IgnisWorld world,
                                   IgnisLocation center,
                                   int scanRadius,
                                   int projectileCount,
                                   double minVelocity,
                                   double maxVelocity) {
        List<IgnisLocation> candidates = collectSolidBlocks(world, center, scanRadius);
        if (candidates.isEmpty()) {
            return;
        }

        int launches = Math.min(Math.max(1, projectileCount), candidates.size());
        Collections.shuffle(candidates, ThreadLocalRandom.current());

        for (int i = 0; i < launches; i++) {
            IgnisLocation block = candidates.get(i);
            String material = world.getBlockMaterialKey(block);
            if (isProtected(material)) {
                continue;
            }

            IgnisLocation spawn = block.add(0.5, 0.5, 0.5);
            double dx = spawn.x() - center.x();
            double dy = spawn.y() - center.y() + 0.25;
            double dz = spawn.z() - center.z();
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length < 0.01) {
                dx = ThreadLocalRandom.current().nextDouble(-1, 1);
                dy = ThreadLocalRandom.current().nextDouble(0.2, 1.0);
                dz = ThreadLocalRandom.current().nextDouble(-1, 1);
                length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            }

            double speed = ThreadLocalRandom.current().nextDouble(minVelocity, maxVelocity);
            double vx = dx / length * speed;
            double vy = dy / length * speed + 0.15;
            double vz = dz / length * speed;

            world.setBlockMaterialKey(block, "air");
            Object debris = world.spawnFallingBlock(spawn, material);
            if (debris != null) {
                world.setEntityVelocity(debris, vx, vy, vz);
            }
            world.spawnParticle(spawn, "CRIT", 4, 0.1, 0.1, 0.1, 0.05);
        }

        world.spawnParticle(center, "EXPLOSION", 8, 0.6, 0.6, 0.6, 0.02);
        world.spawnParticle(center, "SMOKE", 24, 0.8, 0.8, 0.8, 0.04);
    }

    private static List<IgnisLocation> collectSolidBlocks(IgnisWorld world, IgnisLocation center, int radius) {
        List<IgnisLocation> blocks = new ArrayList<>();
        int cx = (int) Math.floor(center.x());
        int cy = (int) Math.floor(center.y());
        int cz = (int) Math.floor(center.z());
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radiusSq) {
                        continue;
                    }
                    IgnisLocation block = new IgnisLocation(world.getUniqueId(), world.getName(),
                            cx + x, cy + y, cz + z, 0f, 0f);
                    String material = world.getBlockMaterialKey(block);
                    if (!isProtected(material) && !"air".equals(material)) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    private static boolean isProtected(String material) {
        return "barrier".equals(material) || "bedrock".equals(material) || "air".equals(material)
                || "cave_air".equals(material) || "void_air".equals(material);
    }
}
