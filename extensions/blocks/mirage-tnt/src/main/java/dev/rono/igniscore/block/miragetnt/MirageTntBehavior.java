package dev.rono.igniscore.block.miragetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;

final class MirageTntBehavior {
    private final IgnisStrategyContext context;

    MirageTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 12 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "END_ROD", 3, 0.5, 0.2, 0.5, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int decoys = StrategySupport.customInt(def, "decoyCount", 3);
        double spread = StrategySupport.customDouble(def, "decoySpread", 6.0);
        float decoyPower = (float) StrategySupport.customDouble(def, "decoyPower", 8.0);
        float realPower = (float) StrategySupport.customDouble(def, "realPower", 2.5);
        PreviewTrickSupport.playDecoyExplosions(context.effects(), world, loc, decoys, spread, decoyPower);
        double offset = StrategySupport.customDouble(def, "realOffset", 4.0);
        IgnisLocation real = loc.add(offset, -1, 0);
        world.playSound(real, "ENTITY_GENERIC_EXPLODE", 0.8f, 1.0f);
        ExplosionSupport.createExplosion(world, real, realPower, false, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
