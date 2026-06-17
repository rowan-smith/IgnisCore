package dev.rono.igniscore.block.ringtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockBlastSupport;
import dev.rono.igniscore.api.util.Locations;

final class RingBehavior {
    private final IgnisStrategyContext context;

    RingBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 6 != 0) {
            return;
        }
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        int major = StrategySupport.customInt(def, "majorRadius", 7);
        double angle = (instance.getTicksLeft() % 360) * Math.PI / 180.0;
        IgnisLocation ringPoint = center.add(Math.cos(angle) * major, 0, Math.sin(angle) * major);
        world.spawnParticle(ringPoint, "FLAME", 2, 0.05, 0.2, 0.05, 0.01);
        world.spawnParticle(center, "SMOKE", 3, 0.2, 0.2, 0.2, 0.0);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        int majorRadius = StrategySupport.customInt(def, "majorRadius", 7);
        int minorRadius = StrategySupport.customInt(def, "minorRadius", 3);
        boolean staggered = StrategySupport.customBoolean(def, "staggered", true);
        int batchSize = StrategySupport.customInt(def, "batchSize", 16);
        int batchDelayTicks = StrategySupport.customInt(def, "batchDelayTicks", 2);

        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 2.2f, 0.95f);
        world.playSound(loc, "BLOCK_BELL_RESONATE", 1.5f, 0.7f);
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI * 2.0 / 12.0;
            IgnisLocation burst = loc.add(Math.cos(angle) * majorRadius, 0, Math.sin(angle) * majorRadius);
            world.spawnParticle(burst, "EXPLOSION", 2, 0.1, 0.3, 0.1, 0.02);
        }

        BlockBlastSupport.breakTorus(context.region(), world, loc, majorRadius, minorRadius,
                staggered, batchSize, batchDelayTicks, context.scheduler());
        world.createExplosion(loc, 1.5f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
