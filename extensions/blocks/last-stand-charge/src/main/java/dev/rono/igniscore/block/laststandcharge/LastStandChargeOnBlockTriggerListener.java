package dev.rono.igniscore.block.laststandcharge;

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

final class LastStandChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    LastStandChargeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = LastStandChargeSupport.worldAt(context, loc);
        world.playSound(loc, "ITEM_TOTEM_USE", 1.0f, 0.8f);
        TheatricsSupport.pulseRing(world, loc, 3.0, "EXPLOSION");
        ExplosionSupport.createExplosion(world, loc, def, StrategySupport.customDouble(def, "lastStandPower", 6.0), false);
    }
}

