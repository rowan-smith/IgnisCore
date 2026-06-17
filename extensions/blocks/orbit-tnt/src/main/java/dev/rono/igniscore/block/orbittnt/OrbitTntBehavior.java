package dev.rono.igniscore.block.orbittnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class OrbitTntBehavior {
    private final IgnisStrategyContext context;

    OrbitTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double radius = StrategySupport.customDouble(def, "orbitRadius", 6.0);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 80);
        EntityPhysicsSupport.orbit(worldAt(center), center, radius, 0.25, elapsed);
        if (instance.getTicksLeft() % 15 == 0) {
            worldAt(center).spawnParticle(center, "END_ROD", 3, radius * 0.3, 0.2, radius * 0.3, 0.01);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(instance.getDefinition(), "orbitRadius", 6.0);
        EntityPhysicsSupport.pushFrom(world, loc, radius, 1.8, true);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 4.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
