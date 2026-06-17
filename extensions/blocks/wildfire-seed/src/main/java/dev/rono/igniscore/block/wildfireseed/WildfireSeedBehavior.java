package dev.rono.igniscore.block.wildfireseed;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class WildfireSeedBehavior {
    private final IgnisStrategyContext context;

    WildfireSeedBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int arms = StrategySupport.customInt(def, "spiralArms", 4);
        int length = StrategySupport.customInt(def, "spiralLength", 6);
        world.playSound(loc, "ITEM_FLINTANDSTEEL_USE", 1.2f, 1.2f);
        BlockTransformSupport.plantWildfireSpiral(world, loc, arms, length);
        ExplosionSupport.createExplosion(world, loc, def, 1.5, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
