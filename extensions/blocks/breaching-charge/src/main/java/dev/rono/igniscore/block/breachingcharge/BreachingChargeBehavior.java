package dev.rono.igniscore.block.breachingcharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.igniscore.api.util.Locations;

final class BreachingChargeBehavior {
    private final IgnisStrategyContext context;

    BreachingChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int radius = StrategySupport.customInt(def, "breachRadius", 6);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.3f, 1.1f);
        BlockTransformSupport.breachingBlast(world, loc, radius);
        world.createExplosion(loc, 2.5f, false, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
