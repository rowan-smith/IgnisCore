package dev.rono.igniscore.block.blinktnt;

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

final class BlinkTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    BlinkTntOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = BlinkTntSupport.worldAt(context, loc);
        EntityUtilSupport.teleportRandomHorizontal(world, loc, StrategySupport.customDouble(def, "blinkRadius", 5.0), 2.5);
        ExplosionSupport.createExplosion(world, loc, def, 3.5, false);
    }
}

