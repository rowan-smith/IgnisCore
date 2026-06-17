package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Block mutation helpers for elemental / corrosive explosives.
 */
public final class BlockTransformSupport {
    private static final Set<String> ACID_TARGETS = Set.of(
            "oak_leaves", "birch_leaves", "spruce_leaves", "jungle_leaves",
            "acacia_leaves", "dark_oak_leaves", "mangrove_leaves", "cherry_leaves",
            "oak_log", "birch_log", "spruce_log", "jungle_log", "acacia_log", "dark_oak_log",
            "oak_planks", "birch_planks", "spruce_planks", "jungle_planks",
            "copper_block", "exposed_copper", "weathered_copper", "oxidized_copper");

    private static final Set<String> BREACHING_WEAK = Set.of(
            "oak_door", "birch_door", "spruce_door", "jungle_door", "acacia_door", "dark_oak_door",
            "iron_door", "oak_trapdoor", "birch_trapdoor", "spruce_trapdoor",
            "glass", "glass_pane", "tinted_glass", "white_stained_glass");

    private BlockTransformSupport() {
    }

    public static void transformSphere(IgnisWorld world,
                                        IgnisLocation center,
                                        int radius,
                                        Predicate<String> matcher,
                                        String replacement) {
        int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > r2) {
                        continue;
                    }
                    IgnisLocation target = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(target));
                    if (matcher.test(material)) {
                        world.setBlockMaterialKey(target, replacement);
                        world.spawnParticle(target.add(0.5, 0.5, 0.5), "SMOKE", 2, 0.1, 0.1, 0.1, 0.01);
                    }
                }
            }
        }
    }

    public static void acidCorrode(IgnisWorld world, IgnisLocation center, int radius) {
        transformSphere(world, center, radius, ACID_TARGETS::contains, "air");
    }

    public static void frostTransform(IgnisWorld world, IgnisLocation center, int radius) {
        int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > r2) {
                        continue;
                    }
                    IgnisLocation target = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(target));
                    if (material.contains("water")) {
                        world.setBlockMaterialKey(target, "ice");
                    } else if (material.equals("lava")) {
                        world.setBlockMaterialKey(target, "obsidian");
                    } else if (material.equals("lava_cauldron")) {
                        world.setBlockMaterialKey(target, "cobblestone");
                    }
                }
            }
        }
    }

    public static void igniteFireRing(IgnisWorld world, IgnisLocation center, int radius) {
        for (int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            IgnisLocation ring = center.add(Math.cos(rad) * radius, 0, Math.sin(rad) * radius);
            if ("air".equals(normalize(world.getBlockMaterialKey(ring)))) {
                world.setBlockMaterialKey(ring, "fire");
            }
        }
    }

    public static void plantWildfireSpiral(IgnisWorld world, IgnisLocation center, int arms, int length) {
        for (int arm = 0; arm < arms; arm++) {
            double base = (Math.PI * 2 * arm) / arms;
            for (int step = 1; step <= length; step++) {
                double angle = base + step * 0.45;
                IgnisLocation point = center.add(Math.cos(angle) * step, 0, Math.sin(angle) * step);
                if ("air".equals(normalize(world.getBlockMaterialKey(point)))) {
                    world.setBlockMaterialKey(point, "fire");
                }
            }
        }
    }

    public static void mudslideFlow(IgnisWorld world,
                                     IgnisLocation center,
                                     int radius,
                                     int depth,
                                     IgnisScheduler scheduler,
                                     int batchDelay) {
        int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > r2) {
                    continue;
                }
                for (int y = 0; y >= -depth; y--) {
                    IgnisLocation target = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(target));
                    if (material.equals("grass_block") || material.equals("dirt") || material.equals("coarse_dirt")) {
                        world.setBlockMaterialKey(target, "mud");
                    }
                }
            }
        }

        int[] step = {0};
        IgnisTask[] ref = {null};
        ref[0] = scheduler.runRepeating(center, () -> {
            if (step[0]++ >= depth) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            int y = -step[0];
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > r2) {
                        continue;
                    }
                    IgnisLocation below = center.add(x, y - 1, z);
                    IgnisLocation current = center.add(x, y, z);
                    if ("mud".equals(normalize(world.getBlockMaterialKey(current)))
                            && "air".equals(normalize(world.getBlockMaterialKey(below)))) {
                        world.setBlockMaterialKey(below, "mud");
                        world.setBlockMaterialKey(current, "air");
                    }
                }
            }
        }, batchDelay, batchDelay);
    }

    public static void tsunamiWave(IgnisWorld world, IgnisLocation center, int radius, IgnisScheduler scheduler) {
        int[] ring = {1};
        IgnisTask[] ref = {null};
        ref[0] = scheduler.runRepeating(center, () -> {
            if (ring[0] > radius) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            int r = ring[0]++;
            for (int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                IgnisLocation point = center.add(Math.cos(rad) * r, 0, Math.sin(rad) * r);
                String material = normalize(world.getBlockMaterialKey(point));
                if (material.equals("air")) {
                    world.setBlockMaterialKey(point, "water");
                }
            }
        }, 2L, 2L);
    }

    public static float breachingPower(BlockDefinition definition, float basePower) {
        return basePower; // multiplier applied per-block in breaching charge behavior
    }

    public static boolean isBreachingWeak(String material) {
        return BREACHING_WEAK.contains(normalize(material));
    }

    public static boolean isBreachingStrong(String material) {
        String m = normalize(material);
        return m.contains("stone") || m.equals("deepslate") || m.equals("bedrock");
    }

    public static void infernoPatch(IgnisWorld world, IgnisLocation center, int radius) {
        int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > r2) {
                    continue;
                }
                for (int y = -2; y <= 2; y++) {
                    IgnisLocation target = center.add(x, y, z);
                    if ("air".equals(normalize(world.getBlockMaterialKey(target)))) {
                        world.setBlockMaterialKey(target, "fire");
                    }
                }
            }
        }
    }

    public static void spreadInferno(IgnisWorld world,
                                    IgnisLocation center,
                                    int radius,
                                    int durationTicks,
                                    IgnisScheduler scheduler) {
        int[] elapsed = {0};
        IgnisTask[] ref = {null};
        ref[0] = scheduler.runRepeating(center, () -> {
            if (elapsed[0]++ >= durationTicks / 10) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            infernoPatch(world, center, radius + elapsed[0]);
            world.spawnParticle(center, "FLAME", 12, radius * 0.4, 0.5, radius * 0.4, 0.03);
        }, 10L, 10L);
    }

    public static void poisonCloud(IgnisWorld world,
                                    IgnisLocation center,
                                    double radius,
                                    int durationTicks,
                                    IgnisScheduler scheduler) {
        int[] elapsed = {0};
        IgnisTask[] ref = {null};
        ref[0] = scheduler.runRepeating(center, () -> {
            if (elapsed[0]++ >= durationTicks) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            world.spawnParticle(center, "ENTITY_EFFECT", 30, radius * 0.35, 0.3, radius * 0.35, 0.02);
            for (IgnisPlayer player : world.getPlayersNear(center, radius)) {
                player.applyPotionEffect("POISON", 40, 0);
            }
        }, 20L, 20L);
    }

    public static void blackHoleCollapse(IgnisRegionService region,
                                          IgnisWorld world,
                                          IgnisLocation center,
                                          int voidRadius,
                                          boolean bedrockShell,
                                          IgnisScheduler scheduler) {
        region.breakWithPredicate(world, center, voidRadius, loc -> true, true, 32, 2, scheduler,
                "PORTAL", "REVERSE_PORTAL");
        if (bedrockShell) {
            int shell = voidRadius + 1;
            int r2 = shell * shell;
            for (int x = -shell; x <= shell; x++) {
                for (int y = -shell; y <= shell; y++) {
                    for (int z = -shell; z <= shell; z++) {
                        int dist2 = x * x + y * y + z * z;
                        if (dist2 < voidRadius * voidRadius || dist2 > r2) {
                            continue;
                        }
                        IgnisLocation target = center.add(x, y, z);
                        if ("air".equals(normalize(world.getBlockMaterialKey(target)))) {
                            world.setBlockMaterialKey(target, "bedrock");
                        }
                    }
                }
            }
        }
    }

    public static void breachingBlast(IgnisWorld world, IgnisLocation center, int radius) {
        int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > r2) {
                        continue;
                    }
                    IgnisLocation target = center.add(x, y, z);
                    String material = normalize(world.getBlockMaterialKey(target));
                    if (isBreachingWeak(material)) {
                        world.setBlockMaterialKey(target, "air");
                        world.spawnParticle(target.add(0.5, 0.5, 0.5), "BLOCK", 4, 0.2, 0.2, 0.2, 0.05);
                    } else if (isBreachingStrong(material)) {
                        continue;
                    }
                }
            }
        }
    }

    public static int customInt(BlockDefinition definition, String key, int defaultValue) {
        return StrategySupport.customInt(definition, key, defaultValue);
    }

    private static String normalize(String materialKey) {
        if (materialKey == null) {
            return "air";
        }
        String normalized = materialKey.toLowerCase(Locale.ROOT);
        int colon = normalized.indexOf(':');
        return colon >= 0 ? normalized.substring(colon + 1) : normalized;
    }
}
