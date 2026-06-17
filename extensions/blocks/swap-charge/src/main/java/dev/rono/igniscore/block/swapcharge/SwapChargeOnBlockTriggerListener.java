package dev.rono.igniscore.block.swapcharge;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class SwapChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    SwapChargeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = SwapChargeSupport.worldAt(context, loc);
        double radius = StrategySupport.customDouble(def, "swapRadius", 8.0);
        EntityUtilSupport.swapNearestPlayers(world, loc, radius);
        ExplosionSupport.createExplosion(world, loc, def, 2.5, false);
    }
}

