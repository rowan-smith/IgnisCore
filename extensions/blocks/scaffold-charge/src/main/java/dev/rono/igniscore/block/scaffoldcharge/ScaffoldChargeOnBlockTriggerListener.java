package dev.rono.igniscore.block.scaffoldcharge;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class ScaffoldChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    ScaffoldChargeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = ScaffoldChargeSupport.worldAt(context, loc);
        world.playSound(loc, "BLOCK_SCAFFOLDING_BREAK", 1.2f, 0.8f);
        ExplosionSupport.createExplosion(world, loc, def, 3.0, false);
    }
}

