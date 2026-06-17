package dev.rono.igniscore.item.decoyflare;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ThrowableSupport;

final class DecoyFlareBehavior {
    private final IgnisStrategyContext context;

    DecoyFlareBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        ThrowableSupport.throwProjectile(context, player, definition, item, (world, impact) -> {
            int duration = StrategySupport.customInt(definition.getCustomData(), "lureDuration", 200);
            double radius = StrategySupport.customDouble(definition.getCustomData(), "lureRadius", 24.0);
            world.playSound(impact, "ENTITY_FIREWORK_ROCKET_LAUNCH", 1.5f, 0.6f);
            world.spawnParticle(impact, "FLAME", 20, 0.5, 0.8, 0.5, 0.05);
            world.spawnParticle(impact, "LAVA", 8, 0.3, 0.3, 0.3, 0.01);
            int[] elapsed = {0};
            IgnisTask[] ref = {null};
            ref[0] = context.scheduler().runRepeating(impact, () -> {
                if (elapsed[0]++ >= duration / 10) {
                    if (ref[0] != null) {
                        ref[0].cancel();
                    }
                    return;
                }
                lureHostiles(world, impact, radius);
                world.spawnParticle(impact, "FLAME", 6, 0.4, 0.5, 0.4, 0.02);
            }, 10L, 10L);
        });
    }

    private void lureHostiles(IgnisWorld world, IgnisLocation target, double radius) {
        for (Object entity : world.getNearbyEntities(target, radius)) {
            String type = entity.getClass().getSimpleName().toLowerCase();
            if (type.contains("zombie") || type.contains("skeleton") || type.contains("creeper")
                    || type.contains("spider") || type.contains("phantom")) {
                world.setEntityTarget(entity, world.getPlayersNear(target, radius).stream().findFirst().orElse(null));
                IgnisLocation loc = world.getEntityLocation(entity);
                if (loc != null) {
                    double dx = target.x() - loc.x();
                    double dz = target.z() - loc.z();
                    double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
                    world.setEntityVelocity(entity, dx / dist * 0.35, 0.1, dz / dist * 0.35);
                }
            }
        }
    }
}
