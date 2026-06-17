package dev.rono.igniscore.block.featherfallcharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class FeatherfallChargeBehavior {
    private final IgnisStrategyContext context;

    FeatherfallChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(def, "featherRadius", 10.0);
        int duration = StrategySupport.customInt(def, "featherDuration", 100);
        world.playSound(loc, "ENTITY_PHANTOM_FLAP", 1.5f, 0.6f);
        world.spawnParticle(loc, "CLOUD", 25, radius * 0.35, 0.3, radius * 0.35, 0.03);
        ExplosionSupport.createExplosion(world, loc, def, 2.0, false);
        EntityPhysicsSupport.applyFeatherfall(world, loc, radius, duration, context.scheduler());
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
