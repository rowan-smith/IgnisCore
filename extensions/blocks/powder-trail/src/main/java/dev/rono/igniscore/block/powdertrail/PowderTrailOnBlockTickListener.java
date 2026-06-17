package dev.rono.igniscore.block.powdertrail;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PowderTrailOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    PowderTrailOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = PowderTrailSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        double trailStep = StrategySupport.customDouble(def, "trailStep", 0.6);
        IgnisLocation trail = loc.add(0, -trailStep * (elapsed / (double) interval), 0);
        world.spawnParticle(trail, "CAMPFIRE_COSY_SMOKE", 3, 0.15, 0.05, 0.15, 0.01);
        if (elapsed % 15 == 0) {
            world.playSound(loc, "BLOCK_SAND_PLACE", 0.5f, 1.4f);
        }
    }
}

