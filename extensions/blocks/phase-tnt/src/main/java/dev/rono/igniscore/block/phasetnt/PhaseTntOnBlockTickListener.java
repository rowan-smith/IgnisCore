package dev.rono.igniscore.block.phasetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PhaseTntOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    PhaseTntOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = PhaseTntSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        world.spawnParticle(loc, "SOUL_FIRE_FLAME", 3, 0.3, 0.2, 0.3, 0.01);
    }
}

