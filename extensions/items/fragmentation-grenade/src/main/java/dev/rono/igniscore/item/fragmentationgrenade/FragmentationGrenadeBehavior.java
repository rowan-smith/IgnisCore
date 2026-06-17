package dev.rono.igniscore.item.fragmentationgrenade;

import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ShrapnelSupport;

final class FragmentationGrenadeBehavior {
    private final IgnisStrategyContext context;

    FragmentationGrenadeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        ThrowableItemConfig throwable = ThrowableItemConfig.from(definition);
        double velocity = throwable.throwVelocity();
        int fuseTicks = throwable.fuseTicks();

        IgnisLocation spawn = player.getEyeLocation();
        double yawRad = Math.toRadians(spawn.yaw());
        double pitchRad = Math.toRadians(spawn.pitch());
        double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * velocity;
        double vy = -Math.sin(pitchRad) * velocity;
        double vz = Math.cos(yawRad) * Math.cos(pitchRad) * velocity;

        IgnisWorld world = player.getWorld();
        Object projectile = world.spawnProjectile("snowball", spawn, player, vx, vy, vz);
        item.setAmount(item.getAmount() - 1);

        int scanRadius = StrategySupport.customInt(definition.getCustomData(), "scanRadius", 4);
        int projectileCount = StrategySupport.customInt(definition.getCustomData(), "projectileCount", 64);
        double minVelocity = StrategySupport.customDouble(definition.getCustomData(), "minVelocity", 0.65);
        double maxVelocity = StrategySupport.customDouble(definition.getCustomData(), "maxVelocity", 1.5);

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
                world.playSound(impact, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.15f);
                ShrapnelSupport.launchDebris(world, impact, scanRadius, projectileCount, minVelocity, maxVelocity);
                world.createExplosion(impact, (float) throwable.power(), throwable.fire(), false);
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
            }
        }, 1L, 1L);
    }
}
