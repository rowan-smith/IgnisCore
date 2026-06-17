package dev.rono.extensions.shared.strategy;

import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

/**
 * Shared snowball-throwable fuse loop for item explosives.
 */
public final class ThrowableSupport {
    @FunctionalInterface
    public interface DetonationHandler {
        void detonate(IgnisWorld world, IgnisLocation impact);
    }

    private ThrowableSupport() {
    }

    public static void throwProjectile(IgnisStrategyContext context,
                                        IgnisPlayer player,
                                        ItemDefinition definition,
                                        IgnisItem item,
                                        DetonationHandler onDetonate) {
        ThrowableItemConfig throwable = ThrowableItemConfig.from(definition);
        IgnisLocation spawn = player.getEyeLocation();
        double yawRad = Math.toRadians(spawn.yaw());
        double pitchRad = Math.toRadians(spawn.pitch());
        double speed = throwable.throwVelocity();
        double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * speed;
        double vy = -Math.sin(pitchRad) * speed;
        double vz = Math.cos(yawRad) * Math.cos(pitchRad) * speed;

        IgnisWorld world = player.getWorld();
        Object projectile = world.spawnProjectile("snowball", spawn, player, vx, vy, vz);
        item.setAmount(item.getAmount() - 1);

        int[] ticks = {0};
        IgnisTask[] taskRef = {null};
        taskRef[0] = context.scheduler().runRepeating(spawn, () -> {
            ticks[0]++;
            if (!world.isEntityValid(projectile) || ticks[0] >= throwable.fuseTicks()) {
                IgnisLocation impact = world.isEntityValid(projectile)
                        ? world.getEntityLocation(projectile)
                        : spawn;
                if (world.isEntityValid(projectile)) {
                    world.removeEntity(projectile);
                }
                onDetonate.detonate(world, impact);
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
            }
        }, 1L, 1L);
    }
}
