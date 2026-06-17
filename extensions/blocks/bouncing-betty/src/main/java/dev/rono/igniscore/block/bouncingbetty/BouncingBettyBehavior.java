package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BuriedMineSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class BouncingBettyBehavior {
    private final IgnisStrategyContext context;

    BouncingBettyBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        double triggerRadius = StrategySupport.customDouble(definition, "triggerRadius", 1.5);
        BuriedMineSupport.arm(context, location, definition.getId(), triggerRadius);
    }

    void onPlacedBreak(IgnisLocation location) {
        BuriedMineSupport.disarm(location);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation blockLoc = instance.getLocation();
        IgnisLocation loc = Locations.toCenter(blockLoc);
        IgnisWorld world = worldAt(loc);
        double popHeight = StrategySupport.customDouble(instance.getDefinition(), "popHeight", 1.2);
        world.playSound(loc, "ENTITY_SLIME_JUMP", 1.5f, 0.4f);
        world.spawnParticle(loc, "CLOUD", 8, 0.2, 0.1, 0.2, 0.05);
        context.scheduler().runLater(loc, () -> {
            IgnisLocation air = loc.add(0, popHeight, 0);
            world.spawnParticle(air, "SMOKE", 6, 0.2, 0.2, 0.2, 0.02);
            world.playSound(air, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.1f);
            ExplosionSupport.createExplosion(world, air, instance.getDefinition(), 3.0, false);
        }, 8L);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
