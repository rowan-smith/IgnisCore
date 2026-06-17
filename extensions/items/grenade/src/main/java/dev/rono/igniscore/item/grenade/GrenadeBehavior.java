package dev.rono.igniscore.item.grenade;

import dev.rono.extensions.shared.ExtensionShared;
import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class GrenadeBehavior {
    private final IgnisStrategyContext context;

    GrenadeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        ThrowableItemConfig throwable = ExtensionShared.config().throwable(definition);
        double velocity = throwable.throwVelocity();
        int fuseTicks = throwable.fuseTicks();

        IgnisLocation spawn = player.getEyeLocation();
        double yawRad = Math.toRadians(spawn.yaw());
        double pitchRad = Math.toRadians(spawn.pitch());
        double speed = velocity;
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
            if (!world.isEntityValid(projectile) || ticks[0] >= fuseTicks) {
                IgnisLocation impact = world.isEntityValid(projectile)
                        ? world.getEntityLocation(projectile)
                        : spawn;
                if (world.isEntityValid(projectile)) {
                    world.removeEntity(projectile);
                }
                world.playSound(impact, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
                ExtensionShared.explosion().create(world, impact, definition, throwable.power(), throwable.fire());
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
            }
        }, 1L, 1L);
    }
}
