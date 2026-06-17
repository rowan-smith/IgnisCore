package dev.rono.igniscore.block.tsunamicharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class TsunamiChargeBehavior {
    private final IgnisStrategyContext context;

    TsunamiChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 20 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "FALLING_WATER", 6, 1, 0.2, 1, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int waveRadius = StrategySupport.customInt(def, "waveRadius", 10);
        world.playSound(loc, "ENTITY_PLAYER_SPLASH", 2.0f, 0.5f);
        BlockTransformSupport.tsunamiWave(world, loc, waveRadius, context.scheduler());
        ExplosionSupport.createExplosion(world, loc, def, 2.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
