package dev.rono.igniscore.block.playerproximityalarm;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PlayerProximityAlarmSupport {
    private PlayerProximityAlarmSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "alarmRadius", 12.0);
          if (!world.getPlayersNear(center, radius).isEmpty()) {
              world.playSound(center, "BLOCK_NOTE_BLOCK_BELL", 1.0f, 0.6f);
              TheatricsSupport.sparkle(world, center, "FIREWORKS_SPARK", 8);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

