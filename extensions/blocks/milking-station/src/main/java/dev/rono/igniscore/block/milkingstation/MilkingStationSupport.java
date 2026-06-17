package dev.rono.igniscore.block.milkingstation;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class MilkingStationSupport {
    private MilkingStationSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        if (EntityUtilSupport.countPassives(world, center, 3.0) > 0) {
              TheatricsSupport.sparkle(world, center, "DRIPPING_HONEY", 4);
              world.playSound(center, "ENTITY_COW_MILK", 0.5f, 1.0f);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

