package dev.rono.igniscore.block.cascademine;

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

final class CascadeMineBehavior {
    private final IgnisStrategyContext context;

    CascadeMineBehavior(IgnisStrategyContext context) {
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
        world.spawnParticle(loc, "LAVA", 2, 0.3, 0.2, 0.3, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float power = ExplosionSupport.resolvePower(def, 3.5);
        int waves = StrategySupport.customInt(def, "cascadeWaves", 4);
        int delay = StrategySupport.customInt(def, "cascadeDelay", 6);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, def, power, false);
        for (int i = 1; i <= waves; i++) {
            final int wave = i;
            context.scheduler().runLater(loc, () -> {
                IgnisLocation ring = loc.add(wave * 1.5, 0, 0);
                world.spawnParticle(ring, "EXPLOSION", 3, 0.4, 0.2, 0.4, 0.02);
                ExplosionSupport.createExplosion(world, ring, power * 0.55f, false, true);
            }, delay * (long) i);
        }
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
