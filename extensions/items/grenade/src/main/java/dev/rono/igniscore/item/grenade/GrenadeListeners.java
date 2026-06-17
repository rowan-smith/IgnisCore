package dev.rono.igniscore.item.grenade;

import dev.rono.extensions.shared.ExtensionShared;
import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class GrenadeListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    GrenadeListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
                ThrowableItemConfig throwable = ExtensionShared.config().throwable(event.definition());
                double velocity = throwable.throwVelocity();
                int fuseTicks = throwable.fuseTicks();

                IgnisLocation spawn = event.player().getEyeLocation();
                double yawRad = Math.toRadians(spawn.yaw());
                double pitchRad = Math.toRadians(spawn.pitch());
                double speed = velocity;
                double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * speed;
                double vy = -Math.sin(pitchRad) * speed;
                double vz = Math.cos(yawRad) * Math.cos(pitchRad) * speed;

                IgnisWorld world = event.player().getWorld();
                Object projectile = world.spawnProjectile("snowball", spawn, event.player(), vx, vy, vz);
                event.item().setAmount(event.item().getAmount() - 1);

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
                        ExtensionShared.explosion().create(world, impact, event.definition(), throwable.power(), throwable.fire());
                        if (taskRef[0] != null) {
                            taskRef[0].cancel();
                        }
                    }
                }, 1L, 1L);
            }
    }
}
