package dev.rono.igniscore.block.frosttnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class FrostTntBehavior {
    private final IgnisStrategyContext context;

    FrostTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 15 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "SNOWFLAKE", 8, 1.5, 0.5, 1.5, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int radius = StrategySupport.customInt(def, "frostRadius", 8);
        double slowRadius = StrategySupport.customDouble(def, "slowRadius", 12.0);
        int slowDuration = StrategySupport.customInt(def, "slowDuration", 100);
        world.playSound(loc, "BLOCK_GLASS_BREAK", 1.5f, 0.4f);
        BlockTransformSupport.frostTransform(world, loc, radius);
        for (IgnisPlayer player : world.getPlayersNear(loc, slowRadius)) {
            player.applyPotionEffect("SLOWNESS", slowDuration, 1);
        }
        world.spawnParticle(loc, "SNOWFLAKE", 40, radius * 0.4, radius * 0.4, radius * 0.4, 0.05);
        ExplosionSupport.createExplosion(world, loc, def, 3.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
