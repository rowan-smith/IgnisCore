package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.List;

/**
 * Entity classification and manipulation helpers.
 */
public final class EntityUtilSupport {
    private EntityUtilSupport() {
    }

    public static boolean isHostile(Object entity) {
        String type = entity.getClass().getSimpleName().toLowerCase();
        return type.contains("zombie") || type.contains("skeleton") || type.contains("creeper")
                || type.contains("spider") || type.contains("phantom") || type.contains("witch")
                || type.contains("pillager") || type.contains("blaze") || type.contains("slime");
    }

    public static boolean isPassive(Object entity) {
        String type = entity.getClass().getSimpleName().toLowerCase();
        return type.contains("cow") || type.contains("sheep") || type.contains("pig")
                || type.contains("chicken") || type.contains("villager") || type.contains("horse");
    }

    public static boolean isLootEntity(Object entity) {
        if (entity instanceof IgnisPlayer) {
            return false;
        }
        String type = entity.getClass().getSimpleName().toLowerCase();
        return type.contains("item") || type.contains("experience") || type.contains("xp");
    }

    public static void pullLoot(IgnisWorld world, IgnisLocation target, double radius, double strength) {
        for (Object entity : world.getNearbyEntities(target, radius)) {
            if (!isLootEntity(entity)) {
                continue;
            }
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc == null) {
                continue;
            }
            double dx = target.x() - loc.x();
            double dy = target.y() - loc.y();
            double dz = target.z() - loc.z();
            double dist = Math.max(0.2, Math.sqrt(dx * dx + dy * dy + dz * dz));
            world.setEntityVelocity(entity, dx / dist * strength, dy / dist * strength, dz / dist * strength);
        }
    }

    public static void freezeInRadius(IgnisWorld world, IgnisLocation center, double radius) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            world.setEntityVelocity(entity, 0, 0, 0);
        }
        for (IgnisPlayer player : world.getPlayersNear(center, radius)) {
            player.applyPotionEffect("SLOWNESS", 20, 3);
            player.applyPotionEffect("JUMP_BOOST", 20, 128);
        }
    }

    public static void teleportRandomHorizontal(IgnisWorld world, IgnisLocation center, double radius, double distance) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc == null) {
                continue;
            }
            double angle = Math.random() * Math.PI * 2;
            IgnisLocation dest = loc.add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
            world.spawnParticle(loc, "PORTAL", 12, 0.3, 0.5, 0.3, 0.1);
            world.setEntityVelocity(entity, Math.cos(angle) * 1.2, 0.4, Math.sin(angle) * 1.2);
            world.spawnParticle(dest, "REVERSE_PORTAL", 8, 0.2, 0.3, 0.2, 0.05);
        }
    }

    public static int countHostiles(IgnisWorld world, IgnisLocation center, double radius) {
        int count = 0;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (isHostile(entity)) {
                count++;
            }
        }
        return count;
    }

    public static int countPassives(IgnisWorld world, IgnisLocation center, double radius) {
        int count = 0;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (isPassive(entity)) {
                count++;
            }
        }
        return count;
    }

    public static void herdPassives(IgnisWorld world, IgnisLocation target, double radius) {
        for (Object entity : world.getNearbyEntities(target, radius)) {
            if (!isPassive(entity)) {
                continue;
            }
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc == null) {
                continue;
            }
            double dx = target.x() - loc.x();
            double dz = target.z() - loc.z();
            double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
            world.setEntityVelocity(entity, dx / dist * 0.3, 0.05, dz / dist * 0.3);
        }
    }

    public static void swapNearestPlayers(IgnisWorld world, IgnisLocation center, double radius) {
        List<IgnisPlayer> players = world.getPlayersNear(center, radius);
        if (players.size() < 2) {
            return;
        }
        IgnisPlayer a = players.get(0);
        IgnisPlayer b = players.get(1);
        IgnisLocation locA = a.getLocation();
        IgnisLocation locB = b.getLocation();
        world.spawnParticle(locA, "END_ROD", 10, 0.2, 0.5, 0.2, 0.05);
        world.spawnParticle(locB, "END_ROD", 10, 0.2, 0.5, 0.2, 0.05);
        a.getWorld().playSound(locA, "ENTITY_ENDERMAN_TELEPORT", 1.0f, 1.2f);
        b.getWorld().playSound(locB, "ENTITY_ENDERMAN_TELEPORT", 1.0f, 0.8f);
    }
}
