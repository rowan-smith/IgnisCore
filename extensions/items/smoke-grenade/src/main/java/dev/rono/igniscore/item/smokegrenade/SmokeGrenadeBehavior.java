package dev.rono.igniscore.item.smokegrenade;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.extensions.shared.strategy.ThrowableSupport;

final class SmokeGrenadeBehavior {
    private final IgnisStrategyContext context;

    SmokeGrenadeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        ThrowableSupport.throwProjectile(context, player, definition, item, (world, impact) -> deploySmoke(world, impact, definition));
    }

    private void deploySmoke(IgnisWorld world, IgnisLocation impact, ItemDefinition definition) {
        double radius = StrategySupport.customDouble(definition.getCustomData(), "smokeRadius", 5.0);
        int duration = StrategySupport.customInt(definition.getCustomData(), "smokeDuration", 120);
        world.playSound(impact, "BLOCK_FIRE_EXTINGUISH", 1.0f, 0.7f);
        int[] elapsed = {0};
        IgnisTask[] ref = {null};
        ref[0] = context.scheduler().runRepeating(impact, () -> {
            if (elapsed[0]++ >= duration / 5) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            world.spawnParticle(impact, "LARGE_SMOKE", 40, radius * 0.4, 0.5, radius * 0.4, 0.02);
            world.spawnParticle(impact, "CLOUD", 20, radius * 0.35, 0.3, radius * 0.35, 0.01);
        }, 5L, 5L);
    }
}
