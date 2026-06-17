package dev.rono.igniscore.block.fishsmokerrack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class FishSmokerRackSupport {
    private FishSmokerRackSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "SMOKE", StrategySupport.customInt(definition, "fishSmoke", 5), 0.3, 0.3, 0.3, 0.02);
          world.playSound(center, "ENTITY_FISH_SWIM", 0.4f, 0.7f);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

