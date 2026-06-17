package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.BuriedMineSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.util.Locations;

final class EmberMineBehavior {
    private final IgnisStrategyContext context;

    EmberMineBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        double triggerRadius = StrategySupport.customDouble(definition, "triggerRadius", 1.0);
        BuriedMineSupport.arm(context, location, definition.getId(), triggerRadius);
        IgnisLocation center = Locations.toCenter(location);
        worldAt(center).spawnParticle(center.add(0, -0.2, 0), "SMOKE", 3, 0.15, 0.05, 0.15, 0.0);
    }

    void onPlacedBreak(IgnisLocation location) {
        BuriedMineSupport.disarm(location);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int ringRadius = StrategySupport.customInt(instance.getDefinition(), "fireRingRadius", 3);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.3f);
        BlockTransformSupport.igniteFireRing(world, loc, ringRadius);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 1.8, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
