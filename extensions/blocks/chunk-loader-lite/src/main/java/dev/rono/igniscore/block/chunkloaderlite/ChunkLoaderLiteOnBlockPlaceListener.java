package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class ChunkLoaderLiteOnBlockPlaceListener implements OnBlockPlaceListener {
    private final ChunkLoaderLiteRuntime runtime;

    ChunkLoaderLiteOnBlockPlaceListener(ChunkLoaderLiteRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        IgnisWorld world = ChunkLoaderLiteSupport.worldAt(runtime, event.block().location());
        world.setChunkForceLoaded(event.block().location(), true);
        runtime.registry.registerBlock(event.block().location(), ChunkLoaderLiteSupport.title(runtime, event.block().definition()), 1);
        long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 40);
        PlacedTickSupport.start(runtime.context, event.block().location(), period, () -> ChunkLoaderLiteSupport.tick(runtime, event.block().definition(), event.block().location()));
        TheatricsSupport.chime(world, Locations.toCenter(event.block().location()), 1.0f);
    }
}

