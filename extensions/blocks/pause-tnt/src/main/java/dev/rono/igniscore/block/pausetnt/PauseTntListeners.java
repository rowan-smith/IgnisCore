package dev.rono.igniscore.block.pausetnt;

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

final class PauseTntListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    PauseTntListeners(IgnisStrategyContext context) {
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
                int pauseAt = StrategySupport.customInt(def, "pauseAtElapsed", fuse / 2);
                if (elapsed == pauseAt) {
                    TheatricsSupport.sparkle(world, loc, "END_ROD", 16);
                    world.playSound(loc, "BLOCK_NOTE_BLOCK_BELL", 1.0f, 1.2f);
                } else if (elapsed > pauseAt && elapsed < pauseAt + StrategySupport.customInt(def, "pauseDuration", 20)) {
                    TheatricsSupport.pulseRing(world, loc, 1.2, "REVERSE_PORTAL");
                }
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
                BlockDefinition def = event.instance().getDefinition();
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 0.9f);
                ExplosionSupport.createExplosion(world, loc, def, 4.5, false);
    }
}
