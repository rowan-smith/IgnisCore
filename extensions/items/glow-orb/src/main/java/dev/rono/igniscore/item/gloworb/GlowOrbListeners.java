package dev.rono.igniscore.item.gloworb;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class GlowOrbListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    GlowOrbListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation eye = event.player().getEyeLocation();
                double speed = StrategySupport.customDouble(event.definition().getCustomData(), "throwSpeed", 1.0);
                Object orb = world.spawnProjectile("snowball", eye, event.player(), 0, 0, speed);
                event.item().setAmount(event.item().getAmount() - 1);
                if (orb == null) {
                    return;
                }
                int duration = StrategySupport.customInt(event.definition().getCustomData(), "glowDurationTicks", 1200);
                IgnisLocation stick = event.clickedBlock() != null ? event.clickedBlock().getLocation() : eye;
                TheatricsSupport.sparkle(world, stick, "END_ROD", 8);
                int[] ticks = {0};
                IgnisTask[] ref = {null};
                ref[0] = context.scheduler().runRepeating(stick, () -> {
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
}
