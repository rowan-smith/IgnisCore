package dev.rono.igniscore.block.hydroponictray;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class HydroponicTraySupport {
    private HydroponicTraySupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "hydroRadius", 2);
          BlockScanSupport.bonemealRadius(world, center, radius);
          world.spawnParticle(center, "DRIPPING_WATER", 6, radius * 0.4, 0.2, radius * 0.4, 0.01);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

