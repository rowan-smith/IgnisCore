package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class AutoSieveListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    AutoSieveListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        PlacedBlock block = event.block();
        long period = context.config.getInt(block.definition(), "tickPeriod", 20);
        PlacedTickSupport.start(context, block.location(), period, () -> tick(block));
        IgnisLocation center = Locations.toCenter(block.location());
        TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
    }

    private void tick(PlacedBlock block) {
        IgnisWorld world = worldAt(block.location());
        IgnisLocation center = Locations.toCenter(block.location());
        TheatricsSupport.sparkle(world, center, "BLOCK", context.config.getInt(block.definition(), "sieveParticles", 6));
        world.playSound(center, "BLOCK_SAND_BREAK", 0.4f, 1.3f);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
