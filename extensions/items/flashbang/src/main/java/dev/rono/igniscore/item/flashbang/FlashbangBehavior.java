package dev.rono.igniscore.item.flashbang;

import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class FlashbangBehavior {
    private final IgnisStrategyContext context;

    FlashbangBehavior(IgnisStrategyContext context) {
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

        double radius = StrategySupport.customDouble(definition.getCustomData(), "radius", 14.0);
        int blindnessDuration = StrategySupport.customInt(definition.getCustomData(), "blindnessDuration", 120);
        int nauseaDuration = StrategySupport.customInt(definition.getCustomData(), "nauseaDuration", 100);
        int darknessDuration = StrategySupport.customInt(definition.getCustomData(), "darknessDuration", 80);

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
                detonate(world, impact, radius, blindnessDuration, nauseaDuration, darknessDuration);
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
            }
        }, 1L, 1L);
    }

    private void detonate(IgnisWorld world,
                           IgnisLocation impact,
                           double radius,
                           int blindnessDuration,
                           int nauseaDuration,
                           int darknessDuration) {
        world.spawnParticle(impact, "FLASH", 2, 0, 0, 0, 0);
        world.spawnParticle(impact, "END_ROD", 40, radius * 0.25, radius * 0.25, radius * 0.25, 0.15);
        world.spawnParticle(impact, "CLOUD", 60, radius * 0.35, radius * 0.2, radius * 0.35, 0.05);
        world.playSound(impact, "ENTITY_GENERIC_EXPLODE", 3.0f, 1.8f);
        world.playSound(impact, "ENTITY_ELDER_GUARDIAN_CURSE", 2.5f, 0.4f);

        context.effects().playFakeExplosion(impact, 6.0f, world.getPlayersNear(impact, radius));

        for (IgnisPlayer target : world.getPlayersNear(impact, radius)) {
            double dist = distance(target.getLocation(), impact);
            double falloff = 1.0 - Math.min(1.0, dist / radius);
            int blindTicks = (int) (blindnessDuration * falloff);
            int sickTicks = (int) (nauseaDuration * falloff);
            int darkTicks = (int) (darknessDuration * falloff);

            if (blindTicks > 0) {
                target.applyPotionEffect("BLINDNESS", blindTicks, 1);
            }
            if (sickTicks > 0) {
                target.applyPotionEffect("NAUSEA", sickTicks, 1);
            }
            if (darkTicks > 0) {
                target.applyPotionEffect("DARKNESS", darkTicks, 0);
            }

            IgnisLocation playerLoc = target.getLocation();
            target.getWorld().playSound(playerLoc, "ENTITY_ELDER_GUARDIAN_CURSE", 2.0f, 0.3f);
            target.getWorld().playSound(playerLoc, "BLOCK_BELL_RESONATE", 1.5f, 0.2f);
        }
    }

    private double distance(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
