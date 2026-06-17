package dev.rono.igniscore.block.doppelgangerblock;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class DoppelgangerBlockBehavior {
    private final IgnisStrategyContext context;

    DoppelgangerBlockBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        IgnisLocation center = Locations.toCenter(location);
        IgnisWorld world = worldAt(center);
        for (IgnisPlayer player : world.getPlayersNear(center, 16)) {
            context.effects().showBlockPreview(player, location, "stone");
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 0.9f);
        world.createExplosion(loc, 3.5f, false, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
