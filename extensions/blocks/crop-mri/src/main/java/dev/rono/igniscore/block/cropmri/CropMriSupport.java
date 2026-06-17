package dev.rono.igniscore.block.cropmri;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class CropMriSupport {
    private CropMriSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "mriRadius", 6);
          int crops = BlockScanSupport.countCrops(world, center, radius);
          TheatricsSupport.scanBeam(world, center, center.add(0, 2, 0), "HAPPY_VILLAGER");
          if (crops > 0) {
              world.playSound(center, "BLOCK_NOTE_BLOCK_PLING", 0.5f, 1.0f + crops * 0.05f);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

