package dev.rono.igniscore.block.acceleratingfusetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class AcceleratingFuseTntOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    AcceleratingFuseTntOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = AcceleratingFuseTntSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        float pitch = 0.6f + (elapsed / (float) Math.max(1, fuse)) * 1.4f;
        int particles = 2 + elapsed / Math.max(1, interval);
        world.spawnParticle(loc, "SMOKE", particles, 0.25, 0.15, 0.25, 0.03);
        if (elapsed % 8 == 0) {
            world.playSound(loc, "BLOCK_NOTE_BLOCK_HAT", 0.7f, pitch);
        }
    }
}

