package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class EruptingOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    EruptingOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        ExplosionSupport.createExplosion(EruptingSupport.worldAt(context, loc), loc, event.instance().getDefinition(), 4.0, false);
    }
}

