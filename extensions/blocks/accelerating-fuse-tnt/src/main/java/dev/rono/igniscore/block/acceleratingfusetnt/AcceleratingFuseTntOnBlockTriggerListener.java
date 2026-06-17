package dev.rono.igniscore.block.acceleratingfusetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class AcceleratingFuseTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    AcceleratingFuseTntOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = AcceleratingFuseTntSupport.worldAt(context, loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.5f, 1.1f);
        TheatricsSupport.sparkle(world, loc, "EXPLOSION", 30);
        ExplosionSupport.createExplosion(world, loc, def, 5.0, false);
    }
}

