package dev.rono.igniscore.block.ricochettnt;

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

final class RicochetTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    RicochetTntOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = RicochetTntSupport.worldAt(context, loc);
        float power = ExplosionSupport.resolvePower(def, 3.0);
        int bounces = StrategySupport.customInt(def, "bounces", 4);
        double step = StrategySupport.customDouble(def, "step", 2.5);
        float yaw = ExplosionVariantsSupport.resolveYaw(world, event.instance().getLocation(), event.triggerContext(), context);
        ExplosionVariantsSupport.ricochetRay(world, loc, yaw, bounces, step, power);
    }
}

