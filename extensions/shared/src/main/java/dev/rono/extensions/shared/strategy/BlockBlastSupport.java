package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * WorldEdit-style shaped block removal without item drops.
 */
public final class BlockBlastSupport {
    private static final int MAX_WORLD_Y = 320;
    private static final int MIN_WORLD_Y = -64;

    private BlockBlastSupport() {
    }

    public static void breakHollowSphere(IgnisWorld world,
                                         IgnisLocation center,
                                         int outerRadius,
                                         int shellThickness,
                                         boolean staggered,
                                         int batchSize,
                                         int batchDelayTicks,
                                         IgnisScheduler scheduler) {
        int innerRadius = Math.max(0, outerRadius - Math.max(1, shellThickness));
        double innerSq = (double) innerRadius * innerRadius;
        double outerSq = (double) outerRadius * outerRadius;

        List<IgnisLocation> targets = new ArrayList<>();
        int cx = (int) Math.floor(center.x());
        int cy = (int) Math.floor(center.y());
        int cz = (int) Math.floor(center.z());

        for (int x = -outerRadius; x <= outerRadius; x++) {
            for (int y = -outerRadius; y <= outerRadius; y++) {
                for (int z = -outerRadius; z <= outerRadius; z++) {
                    double distSq = x * x + y * y + z * z;
                    if (distSq > outerSq || distSq <= innerSq) {
                        continue;
                    }
                    collectBreakable(world, targets, cx + x, cy + y, cz + z);
                }
            }
        }

        breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler, "EXPLOSION", "SMOKE");
    }

    public static void breakTorus(IgnisWorld world,
                                   IgnisLocation center,
                                   int majorRadius,
                                   int minorRadius,
                                   boolean staggered,
                                   int batchSize,
                                   int batchDelayTicks,
                                   IgnisScheduler scheduler) {
        List<IgnisLocation> targets = new ArrayList<>();
        int cx = (int) Math.floor(center.x());
        int cy = (int) Math.floor(center.y());
        int cz = (int) Math.floor(center.z());
        int scan = majorRadius + minorRadius + 1;
        double majorSq = (double) majorRadius * majorRadius;
        double innerMinor = Math.max(1, minorRadius - 2);
        double innerMinorSq = innerMinor * innerMinor;
        double outerMinorSq = (double) minorRadius * minorRadius;

        for (int x = -scan; x <= scan; x++) {
            for (int y = -minorRadius; y <= minorRadius; y++) {
                for (int z = -scan; z <= scan; z++) {
                    double horizontal = Math.sqrt(x * x + z * z);
                    if (horizontal < majorRadius - minorRadius || horizontal > majorRadius + minorRadius) {
                        continue;
                    }

                    double ringOffset = horizontal - majorRadius;
                    double tubeDistSq = ringOffset * ringOffset + y * y;
                    if (tubeDistSq > outerMinorSq || tubeDistSq < innerMinorSq) {
                        continue;
                    }

                    if (x * x + z * z < majorSq * 0.15) {
                        continue;
                    }

                    collectBreakable(world, targets, cx + x, cy + y, cz + z);
                }
            }
        }

        breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler, "FLAME", "SMOKE");
    }

    public static void breakCylinderDown(IgnisWorld world,
                                          IgnisLocation center,
                                          int radius,
                                          int depth,
                                          boolean staggered,
                                          int batchSize,
                                          int batchDelayTicks,
                                          IgnisScheduler scheduler) {
        List<IgnisLocation> targets = new ArrayList<>();
        int cx = (int) Math.floor(center.x());
        int cy = (int) Math.floor(center.y());
        int cz = (int) Math.floor(center.z());
        int radiusSq = radius * radius;

        for (int y = 0; y <= depth; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radiusSq) {
                        continue;
                    }
                    collectBreakable(world, targets, cx + x, cy - y, cz + z);
                }
            }
        }

        breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler, "CLOUD", "CRIT");
    }

    public static void breakUnderwater(IgnisWorld world,
                                        IgnisLocation center,
                                        int radius,
                                        boolean staggered,
                                        int batchSize,
                                        int batchDelayTicks,
                                        IgnisScheduler scheduler) {
        List<IgnisLocation> targets = new ArrayList<>();
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
                    int bx = cx + x;
                    int by = cy + y;
                    int bz = cz + z;
                    if (!isBelowWaterSurface(world, bx, by, bz)) {
                        continue;
                    }
                    collectBreakable(world, targets, bx, by, bz);
                }
            }
        }

        breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler, "BUBBLE", "SPLASH");
    }

    public static void breakWithPredicate(IgnisWorld world,
                                           IgnisLocation center,
                                           int radius,
                                           Predicate<IgnisLocation> predicate,
                                           boolean staggered,
                                           int batchSize,
                                           int batchDelayTicks,
                                           IgnisScheduler scheduler,
                                           String primaryParticle,
                                           String secondaryParticle) {
        List<IgnisLocation> targets = new ArrayList<>();
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
                    IgnisLocation block = blockLocation(world, cx + x, cy + y, cz + z);
                    if (predicate.test(block) && isBreakable(world, block)) {
                        targets.add(block);
                    }
                }
            }
        }

        breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                primaryParticle, secondaryParticle);
    }

    public static boolean isBelowWaterSurface(IgnisWorld world, int x, int y, int z) {
        int waterTop = findWaterSurfaceY(world, x, z);
        return waterTop >= 0 && y <= waterTop;
    }

    public static int findWaterSurfaceY(IgnisWorld world, int x, int z) {
        int waterTop = -1;
        boolean inWater = false;
        for (int y = MAX_WORLD_Y; y >= MIN_WORLD_Y; y--) {
            String material = world.getBlockMaterialKey(blockLocation(world, x, y, z));
            if (isWaterLike(material)) {
                waterTop = y;
                inWater = true;
            } else if (inWater && isAirLike(material)) {
                break;
            } else if (inWater) {
                break;
            }
        }
        return waterTop;
    }

    private static void collectBreakable(IgnisWorld world, List<IgnisLocation> targets, int x, int y, int z) {
        IgnisLocation block = blockLocation(world, x, y, z);
        if (isBreakable(world, block)) {
            targets.add(block);
        }
    }

    private static void breakTargets(IgnisWorld world,
                                     IgnisLocation center,
                                     List<IgnisLocation> targets,
                                     boolean staggered,
                                     int batchSize,
                                     int batchDelayTicks,
                                     IgnisScheduler scheduler,
                                     String primaryParticle,
                                     String secondaryParticle) {
        if (targets.isEmpty()) {
            return;
        }

        targets.sort(Comparator.comparingDouble(loc -> distanceSquared(loc, center)));

        if (!staggered || scheduler == null) {
            for (IgnisLocation target : targets) {
                breakBlock(world, target, primaryParticle, secondaryParticle);
            }
            return;
        }

        int safeBatch = Math.max(1, batchSize);
        int delay = Math.max(1, batchDelayTicks);
        int[] index = {0};
        IgnisTask[] taskRef = {null};
        taskRef[0] = scheduler.runRepeating(center, () -> {
            int end = Math.min(index[0] + safeBatch, targets.size());
            for (int i = index[0]; i < end; i++) {
                breakBlock(world, targets.get(i), primaryParticle, secondaryParticle);
            }
            index[0] = end;
            if (index[0] >= targets.size() && taskRef[0] != null) {
                taskRef[0].cancel();
            }
        }, 0L, delay);
    }

    private static void breakBlock(IgnisWorld world, IgnisLocation block, String primaryParticle, String secondaryParticle) {
        IgnisLocation particleLoc = block.add(0.5, 0.5, 0.5);
        world.spawnParticle(particleLoc, primaryParticle, 3, 0.15, 0.15, 0.15, 0.01);
        world.spawnParticle(particleLoc, secondaryParticle, 2, 0.1, 0.1, 0.1, 0.02);
        world.setBlockMaterialKey(block, "air");
    }

    private static boolean isBreakable(IgnisWorld world, IgnisLocation block) {
        String material = world.getBlockMaterialKey(block);
        return !isAirLike(material) && !"barrier".equals(material) && !"bedrock".equals(material);
    }

    private static boolean isWaterLike(String material) {
        return material.contains("water") || "bubble_column".equals(material) || "kelp".equals(material)
                || "seagrass".equals(material);
    }

    private static boolean isAirLike(String material) {
        return "air".equals(material) || "cave_air".equals(material) || "void_air".equals(material);
    }

    private static IgnisLocation blockLocation(IgnisWorld world, int x, int y, int z) {
        return new IgnisLocation(world.getUniqueId(), world.getName(), x, y, z, 0f, 0f);
    }

    private static double distanceSquared(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return dx * dx + dy * dy + dz * dz;
    }
}
