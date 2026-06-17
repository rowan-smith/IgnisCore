package dev.rono.igniscore.block.mudslidetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class MudslideTntBehavior {
    private final IgnisStrategyContext context;

    MudslideTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 15 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "FALLING_DRIPSTONE_WATER", 4, 1, 0.2, 1, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int radius = StrategySupport.customInt(def, "slideRadius", 6);
        int depth = StrategySupport.customInt(def, "slideDepth", 4);
        int batchDelay = StrategySupport.customInt(def, "batchDelay", 4);
        world.playSound(loc, "BLOCK_MUD_BREAK", 1.5f, 0.6f);
        BlockTransformSupport.mudslideFlow(world, loc, radius, depth, context.scheduler(), batchDelay);
        ExplosionSupport.createExplosion(world, loc, def, 2.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
