package dev.rono.igniscore.item.gloworb;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;

final class GlowOrbBehavior {
    private final IgnisStrategyContext context;

    GlowOrbBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation eye = player.getEyeLocation();
        double speed = StrategySupport.customDouble(definition.getCustomData(), "throwSpeed", 1.0);
        Object orb = world.spawnProjectile("snowball", eye, player, 0, 0, speed);
        item.setAmount(item.getAmount() - 1);
        if (orb == null) {
            return;
        }
        int duration = StrategySupport.customInt(definition.getCustomData(), "glowDurationTicks", 1200);
        IgnisLocation stick = clickedBlock != null ? clickedBlock.getLocation() : eye;
        TheatricsSupport.sparkle(world, stick, "END_ROD", 8);
        int[] ticks = {0};
        IgnisTask[] ref = {null};
        ref[0] = context.getScheduler().runRepeating(stick, () -> {
            ticks[0]++;
            IgnisLocation loc = world.isEntityValid(orb) ? world.getEntityLocation(orb) : stick;
            if (loc != null) {
                world.spawnParticle(loc, "END_ROD", 3, 0.1, 0.1, 0.1, 0.01);
            }
            if (ticks[0] >= duration || !world.isEntityValid(orb)) {
                if (world.isEntityValid(orb)) {
                    world.removeEntity(orb);
                }
                if (ref[0] != null) {
                    ref[0].cancel();
                }
            }
        }, 5L, 10L);
    }
}
