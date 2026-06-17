package dev.rono.igniscore.block.ricochettnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class RicochetTntBehavior {
    private final IgnisStrategyContext context;

    RicochetTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int fuse = ExplosionSupport.fuseTicks(instance, 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        world.spawnParticle(loc, "CRIT", 4, 0.2, 0.1, 0.2, 0.02);
        if (elapsed % 10 == 0) {
            world.playSound(loc, "ENTITY_FIREWORK_ROCKET_BLAST_FAR", 0.6f, 1.0f + elapsed * 0.01f);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float power = ExplosionSupport.resolvePower(def, 3.0);
        int bounces = StrategySupport.customInt(def, "bounces", 4);
        double step = StrategySupport.customDouble(def, "step", 2.5);
        float yaw = ExplosionVariantsSupport.resolveYaw(world, instance.getLocation(), triggerContext, context);
        ExplosionVariantsSupport.ricochetRay(world, loc, yaw, bounces, step, power);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
