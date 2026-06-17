package dev.rono.igniscore.item.smokecan;

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

final class SmokeCanListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    SmokeCanListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation eye = event.player().getEyeLocation();
                double speed = StrategySupport.customDouble(event.definition().getCustomData(), "throwSpeed", 0.9);
                Object can = world.spawnProjectile("snowball", eye, event.player(), 0, 0, speed);
                event.item().setAmount(event.item().getAmount() - 1);
                if (can == null) {
                    return;
                }
                int duration = StrategySupport.customInt(event.definition().getCustomData(), "smokeDurationTicks", 400);
                int[] ticks = {0};
                IgnisTask[] ref = {null};
                ref[0] = context.scheduler().runRepeating(eye, () -> {
                    ticks[0]++;
                    IgnisLocation loc = world.isEntityValid(can) ? world.getEntityLocation(can) : null;
                    if (loc != null) {
                        world.spawnParticle(loc, "CAMPFIRE_COSY_SMOKE", 20, 1.5, 0.5, 1.5, 0.02);
                        world.spawnParticle(loc, "CLOUD", 8, 1.0, 0.3, 1.0, 0.01);
                    }
                    if (ticks[0] >= duration / 10 || (loc == null && ticks[0] > 5)) {
                        if (can != null && world.isEntityValid(can)) {
                            world.removeEntity(can);
                        }
                        if (ref[0] != null) {
                            ref[0].cancel();
                        }
                    }
                }, 2L, 2L);
            }
    }
}
