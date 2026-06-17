package dev.rono.igniscore.block.timedcharge;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;

final class TimedChargeBehavior {
    private Object hologram;
    private final IgnisStrategyContext context;

    TimedChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        int seconds = Math.max(0, (instance.getTicksLeft() + 19) / 20);
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        if (hologram == null && seconds > 0) {
            hologram = PreviewTrickSupport.spawnCountdownHologram(context.hologram(), center, seconds);
        } else {
            PreviewTrickSupport.updateCountdownHologram(context.hologram(), hologram, seconds);
        }
        if (instance.getTicksLeft() % 10 == 0) {
            worldAt(center).spawnParticle(center, "CRIT", 2, 0.2, 0.3, 0.2, 0.01);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        PreviewTrickSupport.deleteHologram(context.hologram(), hologram);
        hologram = null;
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "BLOCK_BEACON_DEACTIVATE", 1.5f, 0.8f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 4.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
