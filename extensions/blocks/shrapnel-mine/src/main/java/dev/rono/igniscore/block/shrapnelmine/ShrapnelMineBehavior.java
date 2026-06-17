package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BuriedMineSupport;
import dev.rono.extensions.shared.strategy.ShrapnelSupport;
import dev.rono.igniscore.api.util.Locations;

final class ShrapnelMineBehavior {
    private final IgnisStrategyContext context;

    ShrapnelMineBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        IgnisLocation center = Locations.toCenter(location);
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center.add(0, -0.2, 0), "CLOUD", 4, 0.2, 0.05, 0.2, 0.0);

        if (StrategySupport.customBoolean(definition, "buried", true)) {
            double triggerRadius = StrategySupport.customDouble(definition, "triggerRadius", 1.2);
            BuriedMineSupport.arm(context, location, definition.getId(), triggerRadius);
        }
    }

    void onPlacedBreak(IgnisLocation location) {
        BuriedMineSupport.disarm(location);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        int scanRadius = StrategySupport.customInt(def, "scanRadius", 5);
        int projectileCount = StrategySupport.customInt(def, "projectileCount", 48);
        double minVelocity = StrategySupport.customDouble(def, "minVelocity", 0.55);
        double maxVelocity = StrategySupport.customDouble(def, "maxVelocity", 1.35);

        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.4f, 1.1f);
        world.playSound(loc, "BLOCK_GRAVEL_BREAK", 1.2f, 0.7f);
        ShrapnelSupport.launchDebris(world, loc, scanRadius, projectileCount, minVelocity, maxVelocity);
        world.createExplosion(loc, 1.5f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
