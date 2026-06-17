package dev.rono.igniscore.block.concussiontnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityBlastSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class ConcussionBehavior {
    private final IgnisStrategyContext context;

    ConcussionBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 8 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "CLOUD", 6, 0.25, 0.25, 0.25, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        double knockbackRadius = StrategySupport.customDouble(def, "knockbackRadius", 18.0);
        double knockbackStrength = StrategySupport.customDouble(def, "knockbackStrength", 3.2);
        float terrainPower = (float) StrategySupport.customDouble(def, "terrainPower", 1.5);

        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 3.0f, 0.7f);
        world.playSound(loc, "ENTITY_DRAGON_FIREBALL_EXPLODE", 2.0f, 0.5f);
        world.spawnParticle(loc, "EXPLOSION_EMITTER", 4, 1.5, 0.8, 1.5, 0.0);
        world.spawnParticle(loc, "CLOUD", 120, knockbackRadius * 0.35, 1.2, knockbackRadius * 0.35, 0.06);

        context.effects().playFakeExplosion(loc, 8.0f, world.getPlayersNear(loc, knockbackRadius));
        EntityBlastSupport.applyKnockback(world, loc, knockbackRadius, knockbackStrength, true);
        ExplosionSupport.createExplosion(world, loc, terrainPower, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
