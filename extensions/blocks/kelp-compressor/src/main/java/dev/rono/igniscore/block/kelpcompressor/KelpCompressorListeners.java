package dev.rono.igniscore.block.kelpcompressor;

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

final class KelpCompressorListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    KelpCompressorListeners(IgnisStrategyContext context) {
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
        world.spawnParticle(center, "BUBBLE", StrategySupport.customInt(definition, "kelpBubbles", 6), 0.3, 0.2, 0.3, 0.02);
          world.playSound(center, "BLOCK_WET_GRASS_BREAK", 0.4f, 0.8f);
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
