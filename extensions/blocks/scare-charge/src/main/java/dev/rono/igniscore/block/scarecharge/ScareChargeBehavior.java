package dev.rono.igniscore.block.scarecharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;

final class ScareChargeBehavior {
    private final IgnisStrategyContext context;

    ScareChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(def, "scareRadius", 50.0);
        float fakePower = (float) StrategySupport.customDouble(def, "fakePower", 12.0);
        PreviewTrickSupport.scareExplosion(context.effects(), context.protocol(),
                world, loc, radius, fakePower);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
