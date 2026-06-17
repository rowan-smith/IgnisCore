package dev.rono.igniscore.block.cavitytnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockBlastSupport;
import dev.rono.igniscore.api.util.Locations;

final class CavityBehavior {
    private final IgnisStrategyContext context;

    CavityBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 10 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "END_ROD", 4, 0.4, 0.4, 0.4, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        int outerRadius = StrategySupport.customInt(def, "outerRadius", 8);
        int shellThickness = StrategySupport.customInt(def, "shellThickness", 2);
        boolean staggered = StrategySupport.customBoolean(def, "staggered", true);
        int batchSize = StrategySupport.customInt(def, "batchSize", 24);
        int batchDelayTicks = StrategySupport.customInt(def, "batchDelayTicks", 2);

        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 2.0f, 0.85f);
        world.spawnParticle(loc, "EXPLOSION_EMITTER", 3, outerRadius * 0.3, outerRadius * 0.3, outerRadius * 0.3, 0.0);
        BlockBlastSupport.breakHollowSphere(context.region(), world, loc, outerRadius, shellThickness,
                staggered, batchSize, batchDelayTicks, context.scheduler());
        world.createExplosion(loc, 2.0f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
