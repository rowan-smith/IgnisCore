package dev.rono.igniscore.block.acidtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class AcidTntBehavior {
    private final IgnisStrategyContext context;

    AcidTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 12 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "SNEEZE", 6, 1, 0.3, 1, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int radius = StrategySupport.customInt(def, "corrodeRadius", 7);
        world.playSound(loc, "BLOCK_FIRE_EXTINGUISH", 1.5f, 0.5f);
        BlockTransformSupport.acidCorrode(world, loc, radius);
        ExplosionSupport.createExplosion(world, loc, def, 2.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
