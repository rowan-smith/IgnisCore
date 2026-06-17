package dev.rono.igniscore.block.echoblast;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;

final class EchoBlastBehavior {
    private final IgnisStrategyContext context;

    EchoBlastBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int bursts = StrategySupport.customInt(def, "echoBursts", 3);
        int delay = StrategySupport.customInt(def, "echoDelay", 15);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, def, 4.0, false);
        PreviewTrickSupport.delayedFakeBursts(context.effects(), world, loc, bursts, delay, context.scheduler());
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
