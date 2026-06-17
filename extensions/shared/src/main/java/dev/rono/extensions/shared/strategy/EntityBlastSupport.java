package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisProtocolService;

import java.util.Collection;

/**
 * Entity-only blast effects: knockback and screen shake.
 */
public final class EntityBlastSupport {
    private EntityBlastSupport() {
    }

    public static void applyKnockback(IgnisWorld world,
                                       IgnisLocation center,
                                       double radius,
                                       double strength,
                                       boolean lift) {
        for (Object entity : world.getNearbyEntities(center, radius)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }

            double dx = entityLoc.x() - center.x();
            double dy = entityLoc.y() - center.y();
            double dz = entityLoc.z() - center.z();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0.1) {
                dx = ThreadLocalRandomSign();
                dy = lift ? 0.6 : 0.1;
                dz = ThreadLocalRandomSign();
                dist = 1.0;
            }

            double falloff = 1.0 - Math.min(1.0, dist / radius);
            double impulse = strength * falloff;
            double vy = lift ? impulse * 0.65 + 0.35 * falloff : dy / dist * impulse * 0.25;
            world.setEntityVelocity(entity, dx / dist * impulse, vy, dz / dist * impulse);
        }
    }

    public static void screenShake(IgnisEffectService effects,
                                    IgnisProtocolService protocol,
                                    IgnisWorld world,
                                    IgnisLocation center,
                                    double radius,
                                    int durationTicks,
                                    int intervalTicks,
                                    float soundVolume,
                                    float soundPitch,
                                    IgnisScheduler scheduler) {
        if (scheduler == null) {
            playShakeBurst(effects, protocol, world, center, radius, soundVolume, soundPitch);
            return;
        }

        int[] ticks = {0};
        IgnisTask[] taskRef = {null};
        taskRef[0] = scheduler.runRepeating(center, () -> {
            playShakeBurst(effects, protocol, world, center, radius, soundVolume, soundPitch);
            ticks[0] += Math.max(1, intervalTicks);
            if (ticks[0] >= durationTicks && taskRef[0] != null) {
                taskRef[0].cancel();
            }
        }, 0L, Math.max(1, intervalTicks));
    }

    public static void violentScreenShake(IgnisEffectService effects,
                                           IgnisProtocolService protocol,
                                           IgnisWorld world,
                                           IgnisLocation center,
                                           double radius,
                                           int durationTicks,
                                           IgnisScheduler scheduler) {
        screenShake(effects, protocol, world, center, radius, durationTicks, 2, 2.5f, 0.35f, scheduler);
    }

    private static void playShakeBurst(IgnisEffectService effects,
                                        IgnisProtocolService protocol,
                                        IgnisWorld world,
                                        IgnisLocation center,
                                        double radius,
                                        float volume,
                                        float pitch) {
        Collection<IgnisPlayer> players = world.getPlayersNear(center, radius);
        effects.playSound(center, "ENTITY_GENERIC_EXPLODE", volume, pitch);
        effects.playSound(center, "ENTITY_LIGHTNING_BOLT_THUNDER", volume * 0.6f, pitch + 0.15f);

        if (protocol != null && protocol.isEnabled()) {
            protocol.sendFakeExplosion(center, 4.0f, players);
        }

        for (IgnisPlayer player : players) {
            IgnisLocation playerLoc = player.getLocation();
            player.getWorld().playSound(playerLoc, "ENTITY_GENERIC_EXPLODE", volume * 0.8f, pitch + 0.3f);
            player.getWorld().playSound(playerLoc, "BLOCK_ANVIL_LAND", volume * 0.35f, 0.5f);
        }
    }

    private static double ThreadLocalRandomSign() {
        return Math.random() < 0.5 ? -1.0 : 1.0;
    }
}
