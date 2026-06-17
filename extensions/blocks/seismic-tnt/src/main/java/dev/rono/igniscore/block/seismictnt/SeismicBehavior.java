package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityBlastSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class SeismicBehavior {
    private final IgnisStrategyContext context;

    SeismicBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 5 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "SMOKE", 4, 0.3, 0.05, 0.3, 0.01);
        world.playSound(center, "BLOCK_STONE_HIT", 0.4f, 0.4f);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation epicenter = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(epicenter);

        int gridSize = StrategySupport.customInt(def, "gridSize", 5);
        double spacing = StrategySupport.customDouble(def, "gridSpacing", 4.0);
        float microPower = (float) StrategySupport.customDouble(def, "microPower", 2.5);
        int waveDelay = StrategySupport.customInt(def, "waveDelayTicks", 3);
        int shakeDuration = StrategySupport.customInt(def, "screenShakeDuration", 60);
        double shakeRadius = StrategySupport.customDouble(def, "screenShakeRadius", 40.0);

        EntityBlastSupport.violentScreenShake(
                context.effects(),
                context.protocol(),
                world,
                epicenter,
                shakeRadius,
                shakeDuration,
                context.scheduler());

        int half = gridSize / 2;
        int[] index = {0};
        int total = gridSize * gridSize;
        IgnisTask[] taskRef = {null};
        taskRef[0] = context.scheduler().runRepeating(epicenter, () -> {
            if (index[0] >= total) {
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
                return;
            }

            int gx = index[0] % gridSize - half;
            int gz = index[0] / gridSize - half;
            index[0]++;

            IgnisLocation blast = epicenter.add(gx * spacing, 0, gz * spacing);
            world.playSound(blast, "ENTITY_GENERIC_EXPLODE", 0.8f, 0.6f + (float) Math.random() * 0.3f);
            world.spawnParticle(blast, "EXPLOSION", 3, 0.4, 0.2, 0.4, 0.02);
            world.spawnParticle(blast, "CLOUD", 12, 0.6, 0.2, 0.6, 0.03);
            ExplosionSupport.createExplosion(world, blast, microPower, false, true);
            EntityBlastSupport.applyKnockback(world, blast, spacing * 1.5, 0.35, false);
        }, 0L, Math.max(1, waveDelay));
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
