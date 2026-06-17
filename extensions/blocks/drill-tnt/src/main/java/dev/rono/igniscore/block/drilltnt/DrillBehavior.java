package dev.rono.igniscore.block.drilltnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockBlastSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class DrillBehavior {
    private final IgnisStrategyContext context;

    DrillBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        int ticksLeft = instance.getTicksLeft();
        int fuse = ExplosionSupport.fuseTicks(instance, 50);
        int elapsed = fuse - ticksLeft;

        if (elapsed % 4 == 0) {
            world.spawnParticle(center.add(0, -elapsed * 0.05, 0), "CRIT", 6, 0.15, 0.4, 0.15, 0.02);
            world.spawnParticle(center, "SMOKE", 4, 0.2, 0.5, 0.2, 0.01);
        }
        if (ticksLeft % 12 == 0) {
            world.playSound(center, "BLOCK_STONE_BREAK", 0.6f, 0.5f + elapsed * 0.02f);
        }
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        int radius = StrategySupport.customInt(def, "radius", 3);
        int depth = StrategySupport.customInt(def, "depth", 32);
        boolean staggered = StrategySupport.customBoolean(def, "staggered", true);
        int batchSize = StrategySupport.customInt(def, "batchSize", 8);
        int batchDelayTicks = StrategySupport.customInt(def, "batchDelayTicks", 1);

        world.playSound(loc, "ENTITY_IRON_GOLEM_ATTACK", 1.5f, 0.6f);
        world.playSound(loc, "BLOCK_ANVIL_LAND", 1.0f, 0.8f);
        world.spawnParticle(loc, "CLOUD", 40, radius * 0.4, 0.2, radius * 0.4, 0.03);

        BlockBlastSupport.breakCylinderDown(context.region(), world, loc, radius, depth,
                staggered, batchSize, batchDelayTicks, context.scheduler());
        world.createExplosion(loc, 1.0f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
