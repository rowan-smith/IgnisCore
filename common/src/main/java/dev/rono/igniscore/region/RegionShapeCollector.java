package dev.rono.igniscore.region;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Collects block locations for shaped region operations.
 */
public final class RegionShapeCollector {
    private static final int MAX_WORLD_Y = 320;
    private static final int MIN_WORLD_Y = -64;

    private RegionShapeCollector() {
    }

    public static List<IgnisLocation> hollowSphere(IgnisWorld world, IgnisLocation center,
                                                    int outerRadius, int shellThickness) {
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
        return targets;
    }

    public static List<IgnisLocation> torus(IgnisWorld world, IgnisLocation center,
                                             int majorRadius, int minorRadius) {
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
        return targets;
    }

    public static List<IgnisLocation> cylinderDown(IgnisWorld world, IgnisLocation center,
                                                    int radius, int depth) {
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
        return targets;
    }

    public static List<IgnisLocation> underwaterSphere(IgnisWorld world, IgnisLocation center, int radius) {
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
        return targets;
    }

    public static List<IgnisLocation> spherePredicate(IgnisWorld world,
                                                     IgnisLocation center,
                                                     int radius,
                                                     Predicate<IgnisLocation> predicate) {
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
        return targets;
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

    static boolean isBreakable(IgnisWorld world, IgnisLocation block) {
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

    static IgnisLocation blockLocation(IgnisWorld world, int x, int y, int z) {
        return new IgnisLocation(world.getUniqueId(), world.getName(), x, y, z, 0f, 0f);
    }
}
