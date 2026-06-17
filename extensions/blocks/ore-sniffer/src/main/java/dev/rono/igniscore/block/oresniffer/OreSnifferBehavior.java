package dev.rono.igniscore.block.oresniffer;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class OreSnifferBehavior {
    private final IgnisStrategyContext context;

    OreSnifferBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        long period = StrategySupport.customInt(definition, "tickPeriod", 20);
        PlacedTickSupport.start(context, location, period, () -> tick(definition, location));
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "scanRadius", 12);
          IgnisLocation ore = BlockScanSupport.findNearestOre(world, center, radius);
          if (ore != null) {
              TheatricsSupport.scanBeam(world, center, ore.add(0.5, 0.5, 0.5), "CRIT");
              world.playSound(center, "BLOCK_AMETHYST_BLOCK_CHIME", 0.6f, 1.4f);
          }
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
