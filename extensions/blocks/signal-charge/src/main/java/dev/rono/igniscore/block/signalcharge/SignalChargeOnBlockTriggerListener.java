package dev.rono.igniscore.block.signalcharge;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class SignalChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    SignalChargeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = SignalChargeSupport.worldAt(context, loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 0.9f);
        ExplosionSupport.createExplosion(world, loc, def, 4.0, false);
    }
}

