package dev.rono.igniscore.block.remotec4;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class RemoteC4Behavior {
    private final IgnisStrategyContext context;

    RemoteC4Behavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.2f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 3.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
