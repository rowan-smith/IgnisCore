package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BuriedMineSupport;
import dev.rono.extensions.shared.strategy.DirectionalBlastSupport;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
import dev.rono.igniscore.api.util.Locations;

final class ClaymoreMineBehavior {
    private final IgnisStrategyContext context;

    ClaymoreMineBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        double triggerRadius = StrategySupport.customDouble(definition, "triggerRadius", 2.0);
        BuriedMineSupport.arm(context, location, definition.getId(), triggerRadius);
    }

    void onPlacedBreak(IgnisLocation location) {
        BuriedMineSupport.disarm(location);
        PlacedMetaSupport.clear(location);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float yaw = PlacedMetaSupport.placementYaw(instance.getLocation(), 0f);
        double range = StrategySupport.customDouble(instance.getDefinition(), "coneRange", 8.0);
        double angle = StrategySupport.customDouble(instance.getDefinition(), "coneAngle", 60.0);
        double strength = StrategySupport.customDouble(instance.getDefinition(), "coneStrength", 2.2);
        world.playSound(loc, "ENTITY_PLAYER_ATTACK_STRONG", 1.5f, 0.7f);
        DirectionalBlastSupport.coneBlast(world, loc, yaw, range, angle, strength);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
