package dev.rono.igniscore.block.splittercharge;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class SplitterChargeListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    SplitterChargeListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
                BlockDefinition def = event.instance().getDefinition();
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
                int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
                int interval = StrategySupport.customInt(def, "tickInterval", 5);
                if (elapsed % interval != 0) {
                    return;
                }
                double spread = StrategySupport.customDouble(def, "splitOffset", 2.5);
                TheatricsSupport.pulseRing(world, loc, spread * 0.5, "SMOKE");
                TheatricsSupport.chime(world, loc, 0.8f + elapsed / (float) fuse);
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
                BlockDefinition def = event.instance().getDefinition();
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                float power = ExplosionSupport.resolvePower(def, 4.0);
                double offset = StrategySupport.customDouble(def, "splitOffset", 2.5);
                ExplosionVariantsSupport.cardinalSplit(world, loc, power, offset);
    }
}
