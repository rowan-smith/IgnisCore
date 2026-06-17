package dev.rono.igniscore.block.perplayerweatherdome;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PerPlayerWeatherDomeSupport {
    private PerPlayerWeatherDomeSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.pulseRing(world, center, StrategySupport.customDouble(definition, "domeRadius", 5.0), "CLOUD");
          world.spawnParticle(center.add(0, 3, 0), "RAIN", 8, 2, 0.1, 2, 0.01);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

