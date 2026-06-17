package dev.rono.igniscore.block.mirrorworldtnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class MirrorWorldTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    MirrorWorldTntOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = MirrorWorldTntSupport.worldAt(context, loc);
        float power = ExplosionSupport.resolvePower(def, 4.0);
        double mirrorY = StrategySupport.customDouble(def, "mirrorY", loc.y());
        ExplosionVariantsSupport.mirrorBlast(world, loc, power, mirrorY);
    }
}

