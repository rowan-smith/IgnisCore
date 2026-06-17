package dev.rono.igniscore.block.magnettnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class MagnetTntBehavior {
    private final IgnisStrategyContext context;

    MagnetTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double radius = StrategySupport.customDouble(def, "pullRadius", 8.0);
        double strength = StrategySupport.customDouble(def, "pullStrength", 0.35);
        EntityPhysicsSupport.pullLootToward(worldAt(center), center, radius, strength);
        if (instance.getTicksLeft() % 10 == 0) {
            worldAt(center).spawnParticle(center, "ENCHANT", 8, radius * 0.25, 0.2, radius * 0.25, 0.5);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(def, "scatterRadius", 6.0);
        double strength = StrategySupport.customDouble(def, "scatterStrength", 1.6);
        world.playSound(loc, "BLOCK_BEACON_POWER_SELECT", 1.5f, 1.8f);
        EntityPhysicsSupport.scatterLoot(world, loc, radius, strength);
        ExplosionSupport.createExplosion(world, loc, def, 2.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
