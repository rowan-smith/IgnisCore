package dev.rono.igniscore.block.powdertrail;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PowderTrailOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    PowderTrailOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = PowderTrailSupport.worldAt(context, loc);
        world.playSound(loc, "ENTITY_TNT_PRIMED", 1.0f, 0.8f);
        TheatricsSupport.sparkle(world, loc, "FLAME", 24);
        ExplosionSupport.createExplosion(world, loc, def, 4.0, StrategySupport.customBoolean(def, "fire", false));
    }
}

