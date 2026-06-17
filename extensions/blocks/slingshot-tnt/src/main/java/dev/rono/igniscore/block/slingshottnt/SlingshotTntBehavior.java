package dev.rono.igniscore.block.slingshottnt;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.DirectionalBlastSupport;
import dev.rono.igniscore.api.util.Locations;

final class SlingshotTntBehavior {
    private final IgnisStrategyContext context;

    SlingshotTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 12 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "CRIT", 4, 0.3, 0.2, 0.3, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float yaw = DirectionalBlastSupport.resolveYaw(context, triggerContext, instance.getLocation());
        int steps = StrategySupport.customInt(instance.getDefinition(), "blastSteps", 4);
        double stepDistance = StrategySupport.customDouble(instance.getDefinition(), "stepDistance", 2.5);
        float power = (float) StrategySupport.customDouble(instance.getDefinition(), "power", 3.5);
        world.playSound(loc, "ENTITY_FIREWORK_ROCKET_BLAST", 1.5f, 0.8f);
        DirectionalBlastSupport.forwardBlast(world, loc, yaw, steps, stepDistance, power, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
