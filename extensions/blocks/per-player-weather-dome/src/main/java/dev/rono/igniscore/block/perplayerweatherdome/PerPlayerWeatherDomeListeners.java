package dev.rono.igniscore.block.perplayerweatherdome;

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

final class PerPlayerWeatherDomeListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    PerPlayerWeatherDomeListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.pulseRing(world, center, StrategySupport.customDouble(definition, "domeRadius", 5.0), "CLOUD");
          world.spawnParticle(center.add(0, 3, 0), "RAIN", 8, 2, 0.1, 2, 0.01);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 20);
                PlacedTickSupport.start(context, event.block().location(), period, () -> tick(event.block().definition(), event.block().location()));
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
    }
}
