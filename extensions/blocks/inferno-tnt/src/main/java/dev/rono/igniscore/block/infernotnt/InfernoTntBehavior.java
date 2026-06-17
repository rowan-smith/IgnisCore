package dev.rono.igniscore.block.infernotnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class InfernoTntBehavior {
    private final IgnisStrategyContext context;

    InfernoTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 20 != 0) {
            return;
        }
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        int radius = StrategySupport.customInt(def, "fireRadius", 10);
        worldAt(center).spawnParticle(center, "FLAME", 10, radius * 0.4, 0.3, radius * 0.4, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int radius = StrategySupport.customInt(def, "fireRadius", 12);
        int spreadDuration = StrategySupport.customInt(def, "spreadDuration", 600);
        world.playSound(loc, "ITEM_FIRECHARGE_USE", 2.0f, 0.5f);
        BlockTransformSupport.infernoPatch(world, loc, radius);
        BlockTransformSupport.spreadInferno(world, loc, radius, spreadDuration, context.scheduler());
        ExplosionSupport.createExplosion(world, loc, def, 3.0, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
