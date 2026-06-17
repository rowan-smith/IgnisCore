package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockBlastSupport;
import dev.rono.igniscore.api.util.Locations;

final class DepthChargeBehavior {
    private final IgnisStrategyContext context;

    DepthChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 6 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "BUBBLE", 8, 0.3, 0.3, 0.3, 0.02);
        world.playSound(center, "BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE", 0.5f, 1.2f);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        int radius = StrategySupport.customInt(def, "radius", 7);
        boolean staggered = StrategySupport.customBoolean(def, "staggered", true);
        int batchSize = StrategySupport.customInt(def, "batchSize", 12);
        int batchDelayTicks = StrategySupport.customInt(def, "batchDelayTicks", 1);

        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 0.7f);
        world.playSound(loc, "ENTITY_PLAYER_SPLASH", 1.5f, 0.6f);
        world.spawnParticle(loc, "SPLASH", 40, radius * 0.4, 0.5, radius * 0.4, 0.1);
        world.spawnParticle(loc, "BUBBLE", 60, radius * 0.5, radius * 0.5, radius * 0.5, 0.05);

        BlockBlastSupport.breakUnderwater(context.region(), world, loc, radius,
                staggered, batchSize, batchDelayTicks, context.scheduler());
        world.createExplosion(loc, 0.5f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
