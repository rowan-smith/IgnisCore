package dev.rono.igniscore.block.mobradar;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class MobRadarSupport {
    private MobRadarSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "radarRadius", 16.0);
          int hostiles = EntityUtilSupport.countHostiles(world, center, radius);
          if (hostiles > 0) {
              TheatricsSupport.pulseRing(world, center, Math.min(radius, 4 + hostiles), "CRIMSON_SPORE");
              world.playSound(center, "BLOCK_NOTE_BLOCK_BASS", 0.6f, 0.5f + hostiles * 0.05f);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

