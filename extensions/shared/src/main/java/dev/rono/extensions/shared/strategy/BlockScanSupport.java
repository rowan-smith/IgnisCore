package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.Locale;
import java.util.Set;

/**
 * Block scanning helpers for ore sniffers, crop tools, and material transforms.
 */
public final class BlockScanSupport {
    private static final Set<String> ORE_HINTS = Set.of(
            "coal_ore", "deepslate_coal_ore", "iron_ore", "deepslate_iron_ore",
            "copper_ore", "deepslate_copper_ore", "gold_ore", "deepslate_gold_ore",
            "redstone_ore", "deepslate_redstone_ore", "lapis_ore", "deepslate_lapis_ore",
            "diamond_ore", "deepslate_diamond_ore", "emerald_ore", "deepslate_emerald_ore",
            "nether_gold_ore", "nether_quartz_ore", "ancient_debris");

    private static final Set<String> CROP_HINTS = Set.of(
            "wheat", "carrots", "potatoes", "beetroots", "nether_wart", "melon_stem", "pumpkin_stem");

    private BlockScanSupport() {
    }

    public static IgnisLocation findNearestMatching(IgnisWorld world, IgnisLocation center, int radius, Set<String> targets) {
        IgnisLocation nearest = null;
        double best = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    IgnisLocation probe = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(probe));
                    if (!targets.contains(material)) {
                        continue;
                    }
                    double dist = distance(center, probe);
                    if (dist < best) {
                        best = dist;
                        nearest = probe;
                    }
                }
            }
        }
        return nearest;
    }

    public static IgnisLocation findNearestOre(IgnisWorld world, IgnisLocation center, int radius) {
        return findNearestMatching(world, center, radius, ORE_HINTS);
    }

    public static int countCrops(IgnisWorld world, IgnisLocation center, int radius) {
        int count = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    String material = normalize(world.getBlockMaterialKey(center.add(x, y, z)));
                    if (CROP_HINTS.contains(material)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static void bonemealRadius(IgnisWorld world, IgnisLocation center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                IgnisLocation soil = center.add(x, 0, z);
                IgnisLocation crop = soil.add(0, 1, 0);
                String material = normalize(world.getBlockMaterialKey(crop));
                if (CROP_HINTS.contains(material)) {
                    world.spawnParticle(crop.add(0.5, 0.5, 0.5), "HAPPY_VILLAGER", 4, 0.2, 0.2, 0.2, 0.01);
                }
            }
        }
    }

    public static void mossifyNearWater(IgnisWorld world, IgnisLocation center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                IgnisLocation target = center.add(x, 0, z);
                String material = normalize(world.getBlockMaterialKey(target));
                if (material.equals("stone") || material.equals("cobblestone")) {
                    if (hasWaterNearby(world, target, 2)) {
                        world.setBlockMaterialKey(target, "moss_block");
                        world.spawnParticle(target.add(0.5, 0.5, 0.5), "SPORE_BLOSSOM_AIR", 2, 0.1, 0.1, 0.1, 0.01);
                    }
                }
            }
        }
    }

    public static void deoxidizeCopper(IgnisWorld world, IgnisLocation center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    IgnisLocation target = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(target));
                    if (material.contains("oxidized_copper") || material.contains("weathered_copper")
                            || material.contains("exposed_copper")) {
                        world.setBlockMaterialKey(target, "copper_block");
                        world.spawnParticle(target.add(0.5, 0.5, 0.5), "WAX_ON", 4, 0.1, 0.1, 0.1, 0.01);
                    }
                }
            }
        }
    }

    private static boolean hasWaterNearby(IgnisWorld world, IgnisLocation center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    String material = normalize(world.getBlockMaterialKey(center.add(x, y, z)));
                    if (material.contains("water")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String normalize(String materialKey) {
        if (materialKey == null) {
            return "air";
        }
        String normalized = materialKey.toLowerCase(Locale.ROOT);
        int colon = normalized.indexOf(':');
        return colon >= 0 ? normalized.substring(colon + 1) : normalized;
    }

    private static double distance(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
