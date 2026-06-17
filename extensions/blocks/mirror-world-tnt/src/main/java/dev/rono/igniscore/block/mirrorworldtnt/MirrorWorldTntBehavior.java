package dev.rono.igniscore.block.mirrorworldtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class MirrorWorldTntBehavior {
    private final IgnisStrategyContext context;

    MirrorWorldTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int fuse = ExplosionSupport.fuseTicks(instance, 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        double mirrorY = StrategySupport.customDouble(def, "mirrorY", loc.y());
        IgnisLocation mirror = loc.add(0, (mirrorY * 2) - loc.y() - loc.y(), 0);
        world.spawnParticle(mirror, "END_ROD", 2, 0.1, 0.1, 0.1, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float power = ExplosionSupport.resolvePower(def, 4.0);
        double mirrorY = StrategySupport.customDouble(def, "mirrorY", loc.y());
        ExplosionVariantsSupport.mirrorBlast(world, loc, power, mirrorY);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
