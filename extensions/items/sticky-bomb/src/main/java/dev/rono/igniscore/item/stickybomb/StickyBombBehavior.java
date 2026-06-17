package dev.rono.igniscore.item.stickybomb;

import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;

final class StickyBombBehavior {
    private final IgnisStrategyContext context;

    StickyBombBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
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

        int fuseTicks = StrategySupport.customInt(definition.getCustomData(), "stickFuseTicks", 60);
        int[] ticks = {0};
        IgnisLocation[] stuckAt = {null};
        IgnisTask[] taskRef = {null};
        taskRef[0] = context.scheduler().runRepeating(spawn, () -> {
            ticks[0]++;
            if (!world.isEntityValid(projectile)) {
                IgnisLocation impact = stuckAt[0] != null ? stuckAt[0] : spawn;
                if (ticks[0] >= fuseTicks) {
                    detonate(world, impact, definition, throwable);
                }
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
                return;
            }
            IgnisLocation current = world.getEntityLocation(projectile);
            if (current != null && stuckAt[0] == null) {
                IgnisLocation below = current.add(0, -0.5, 0);
                String material = world.getBlockMaterialKey(below);
                if (material != null && !material.endsWith("air")) {
                    stuckAt[0] = below;
                    world.removeEntity(projectile);
                    world.spawnParticle(stuckAt[0], "SMOKE", 4, 0.1, 0.1, 0.1, 0.01);
                    ticks[0] = 0;
                }
            }
            if (stuckAt[0] != null && ticks[0] >= fuseTicks) {
                detonate(world, stuckAt[0], definition, throwable);
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
            }
        }, 1L, 1L);
    }

    private void detonate(IgnisWorld world, IgnisLocation impact, ItemDefinition definition, ThrowableItemConfig throwable) {
        world.playSound(impact, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        ExplosionSupport.createExplosion(world, impact, definition, throwable.power(), throwable.fire());
    }
}
