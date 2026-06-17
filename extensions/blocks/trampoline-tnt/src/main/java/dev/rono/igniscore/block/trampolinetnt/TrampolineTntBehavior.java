package dev.rono.igniscore.block.trampolinetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.igniscore.api.util.Locations;

final class TrampolineTntBehavior {
    private final IgnisStrategyContext context;

    TrampolineTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 5 != 0) {
            return;
        }
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double radius = StrategySupport.customDouble(def, "radius", 8.0);
        double bounce = StrategySupport.customDouble(def, "launchVelocity", 1.2);
        EntityPhysicsSupport.launchUpward(worldAt(center), center, radius, bounce * 0.15);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(def, "radius", 8.0);
        double velocity = StrategySupport.customDouble(def, "launchVelocity", 2.4);
        world.playSound(loc, "ENTITY_SLIME_JUMP", 2.0f, 0.6f);
        world.spawnParticle(loc, "CLOUD", 20, radius * 0.3, 0.2, radius * 0.3, 0.05);
        EntityPhysicsSupport.launchUpward(world, loc, radius, velocity);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
