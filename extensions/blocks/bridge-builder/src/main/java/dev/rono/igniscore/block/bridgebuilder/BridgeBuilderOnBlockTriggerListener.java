package dev.rono.igniscore.block.bridgebuilder;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class BridgeBuilderOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    BridgeBuilderOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = BridgeBuilderSupport.worldAt(context, loc);
        world.playSound(loc, "BLOCK_WOOD_PLACE", 1.0f, 0.7f);
        ExplosionSupport.createExplosion(world, loc, def, 2.5, false);
    }
}

