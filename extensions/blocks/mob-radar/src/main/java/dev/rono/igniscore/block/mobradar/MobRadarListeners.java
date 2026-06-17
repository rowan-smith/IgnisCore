package dev.rono.igniscore.block.mobradar;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class MobRadarListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    MobRadarListeners(IgnisStrategyContext context) {
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
        double radius = StrategySupport.customDouble(definition, "radarRadius", 16.0);
          int hostiles = EntityUtilSupport.countHostiles(world, center, radius);
          if (hostiles > 0) {
              TheatricsSupport.pulseRing(world, center, Math.min(radius, 4 + hostiles), "CRIMSON_SPORE");
              world.playSound(center, "BLOCK_NOTE_BLOCK_BASS", 0.6f, 0.5f + hostiles * 0.05f);
          }
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        onPlaced(event.block().definition(), event.block().location());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        onPlacedBreak(event.block().definition(), event.block().location());
    }
}
