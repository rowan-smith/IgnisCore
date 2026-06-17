package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity motion helpers for tactical explosives.
 */
public final class EntityPhysicsSupport {
    private EntityPhysicsSupport() {
    }

    public static void pullToward(IgnisWorld world, IgnisLocation target, double radius, double strength) {
        for (Object entity : world.getNearbyEntities(target, radius)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = target.x() - entityLoc.x();
            double dy = target.y() - entityLoc.y();
            double dz = target.z() - entityLoc.z();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0.15 || dist > radius) {
                continue;
            }
            double falloff = 1.0 - dist / radius;
            double impulse = strength * falloff;
            world.setEntityVelocity(entity, dx / dist * impulse, dy / dist * impulse, dz / dist * impulse);
        }
    }

    public static void pushFrom(IgnisWorld world, IgnisLocation source, double radius, double strength, boolean lift) {
        EntityBlastSupport.applyKnockback(world, source, radius, strength, lift);
    }

    public static void orbit(IgnisWorld world,
                              IgnisLocation center,
                              double radius,
                              double angularSpeed,
                              int elapsedTicks) {
        double angle = elapsedTicks * angularSpeed;
        for (Object entity : world.getNearbyEntities(center, radius + 2.0)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = entityLoc.x() - center.x();
            double dz = entityLoc.z() - center.z();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.5 || dist > radius + 1.5) {
                continue;
            }
            double tangentX = -dz / dist;
            double tangentZ = dx / dist;
            double pull = (radius - dist) * 0.08;
            double pullX = dx / dist * pull;
            double pullZ = dz / dist * pull;
            world.setEntityVelocity(
                    entity,
                    tangentX * angularSpeed * 0.35 + pullX,
                    0.02,
                    tangentZ * angularSpeed * 0.35 + pullZ);
        }
    }

    public static void spiralKnockback(IgnisWorld world, IgnisLocation center, double radius, double strength, int tick) {
        double baseAngle = tick * 0.35;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = entityLoc.x() - center.x();
            double dz = entityLoc.z() - center.z();
            double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
            if (dist > radius) {
                continue;
            }
            double angle = baseAngle + Math.atan2(dz, dx);
            double vx = Math.cos(angle) * strength;
            double vz = Math.sin(angle) * strength;
            double outward = strength * (1.0 - dist / radius) * 0.35;
            world.setEntityVelocity(entity, vx + dx / dist * outward, 0.15, vz + dz / dist * outward);
        }
    }

    public static void launchUpward(IgnisWorld world, IgnisLocation center, double radius, double velocity) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dist = horizontalDistance(entityLoc, center);
            if (dist > radius) {
                continue;
            }
            double falloff = 1.0 - Math.min(1.0, dist / radius);
            world.setEntityVelocity(entity, 0, velocity * falloff, 0);
        }
    }

    public static void applyLevitation(IgnisWorld world, IgnisLocation center, double radius, double upward) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            if (horizontalDistance(entityLoc, center) > radius) {
                continue;
            }
            world.setEntityVelocity(entity, 0, upward, 0);
        }
    }

    public static void freezeEntities(IgnisWorld world, IgnisLocation center, double radius, int durationTicks) {
        for (IgnisPlayer player : world.getPlayersNear(center, radius)) {
            player.applyPotionEffect("SLOWNESS", durationTicks, 10);
            player.applyPotionEffect("JUMP_BOOST", durationTicks, 128);
        }
        for (Object entity : world.getNearbyEntities(center, radius)) {
            world.setEntityVelocity(entity, 0, 0, 0);
        }
    }

    public static void snapDamage(IgnisWorld world,
                                   Map<Object, IgnisLocation> anchors,
                                   IgnisLocation center,
                                   double damageRadius) {
        for (Map.Entry<Object, IgnisLocation> entry : anchors.entrySet()) {
            Object entity = entry.getKey();
            IgnisLocation start = entry.getValue();
            IgnisLocation current = world.getEntityLocation(entity);
            if (current == null) {
                continue;
            }
            double stretched = distance(start, current);
            world.spawnParticle(current, "CRIT", 6, 0.1, 0.1, 0.1, 0.05);
            if (stretched > 4.0 && entity instanceof IgnisPlayer player) {
                player.applyPotionEffect("INSTANT_DAMAGE", 1, (int) Math.min(3, stretched / 4.0));
            }
        }
        world.spawnParticle(center, "SOUL_FIRE_FLAME", 20, damageRadius * 0.3, 0.4, damageRadius * 0.3, 0.02);
    }

    public static Map<Object, IgnisLocation> snapshotPositions(IgnisWorld world, IgnisLocation center, double radius) {
        Map<Object, IgnisLocation> snapshot = new HashMap<>();
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc != null) {
                snapshot.put(entity, loc);
            }
        }
        return snapshot;
    }

    public static boolean isLootEntity(Object entity) {
        if (entity instanceof IgnisPlayer) {
            return false;
        }
        String name = entity.getClass().getSimpleName().toLowerCase();
        return name.contains("item") || name.contains("experience") || name.contains("xp");
    }

    public static void pullLootToward(IgnisWorld world, IgnisLocation target, double radius, double strength) {
        for (Object entity : world.getNearbyEntities(target, radius)) {
            if (!isLootEntity(entity)) {
                continue;
            }
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = target.x() - entityLoc.x();
            double dy = target.y() - entityLoc.y();
            double dz = target.z() - entityLoc.z();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0.15 || dist > radius) {
                continue;
            }
            double falloff = 1.0 - dist / radius;
            double impulse = strength * falloff;
            world.setEntityVelocity(entity, dx / dist * impulse, dy / dist * impulse, dz / dist * impulse);
        }
    }

    public static void scatterLoot(IgnisWorld world, IgnisLocation center, double radius, double strength) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (!isLootEntity(entity)) {
                continue;
            }
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = entityLoc.x() - center.x();
            double dy = entityLoc.y() - center.y() + 0.5;
            double dz = entityLoc.z() - center.z();
            double dist = Math.max(0.3, Math.sqrt(dx * dx + dy * dy + dz * dz));
            double impulse = strength * (1.0 - Math.min(1.0, dist / radius));
            world.setEntityVelocity(entity, dx / dist * impulse, 0.35 + impulse * 0.5, dz / dist * impulse);
            world.spawnParticle(entityLoc, "HAPPY_VILLAGER", 2, 0.1, 0.1, 0.1, 0.01);
        }
    }

    public static void applyFeatherfall(IgnisWorld world,
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
            for (Object entity : world.getNearbyEntities(center, radius)) {
                IgnisLocation entityLoc = world.getEntityLocation(entity);
                if (entityLoc == null || horizontalDistance(entityLoc, center) > radius) {
                    continue;
                }
                world.setEntityVelocity(entity, 0, 0.12, 0);
            }
            world.spawnParticle(center, "CLOUD", 6, radius * 0.3, 0.2, radius * 0.3, 0.01);
        }, 1L, 1L);
    }

    public static void coneKnockback(IgnisWorld world,
                                      IgnisLocation origin,
                                      float yawDegrees,
                                      double coneAngleDegrees,
                                      double range,
                                      double strength) {
        double yawRad = Math.toRadians(yawDegrees);
        double facingX = -Math.sin(yawRad);
        double facingZ = Math.cos(yawRad);
        double cosLimit = Math.cos(Math.toRadians(coneAngleDegrees / 2.0));

        for (Object entity : world.getNearbyEntities(origin, range)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = entityLoc.x() - origin.x();
            double dz = entityLoc.z() - origin.z();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.1 || dist > range) {
                continue;
            }
            double dot = (dx / dist) * facingX + (dz / dist) * facingZ;
            if (dot < cosLimit) {
                continue;
            }
            double falloff = 1.0 - dist / range;
            world.setEntityVelocity(entity, facingX * strength * falloff, 0.25, facingZ * strength * falloff);
        }
    }

    private static double horizontalDistance(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static double distance(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
