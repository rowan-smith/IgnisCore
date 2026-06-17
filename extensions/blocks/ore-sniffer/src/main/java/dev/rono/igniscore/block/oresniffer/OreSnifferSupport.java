package dev.rono.igniscore.block.oresniffer;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class OreSnifferSupport {
    private OreSnifferSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "scanRadius", 12);
          IgnisLocation ore = BlockScanSupport.findNearestOre(world, center, radius);
          if (ore != null) {
              TheatricsSupport.scanBeam(world, center, ore.add(0.5, 0.5, 0.5), "CRIT");
              world.playSound(center, "BLOCK_AMETHYST_BLOCK_CHIME", 0.6f, 1.4f);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

