package dev.rono.igniscore.block.centrifugetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class CentrifugeTntBehavior {
    private final IgnisStrategyContext context;

    CentrifugeTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double radius = StrategySupport.customDouble(def, "spinRadius", 9.0);
        double strength = StrategySupport.customDouble(def, "spinStrength", 0.9);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 70);
        EntityPhysicsSupport.spiralKnockback(worldAt(center), center, radius, strength, elapsed);
        if (instance.getTicksLeft() % 6 == 0) {
            worldAt(center).spawnParticle(center, "SWEEP_ATTACK", 2, radius * 0.3, 0.1, radius * 0.3, 0.01);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(instance.getDefinition(), "spinRadius", 9.0);
        EntityPhysicsSupport.spiralKnockback(world, loc, radius, 1.4, instance.getTicksLeft());
        world.playSound(loc, "ENTITY_PLAYER_ATTACK_SWEEP", 1.5f, 0.5f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 2.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
