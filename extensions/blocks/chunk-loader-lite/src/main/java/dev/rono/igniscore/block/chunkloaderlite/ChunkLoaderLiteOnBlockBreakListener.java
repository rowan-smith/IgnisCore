package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.port.IgnisWorld;

final class ChunkLoaderLiteOnBlockBreakListener implements OnBlockBreakListener {
    private final ChunkLoaderLiteRuntime runtime;

    ChunkLoaderLiteOnBlockBreakListener(ChunkLoaderLiteRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        IgnisWorld world = ChunkLoaderLiteSupport.worldAt(runtime, event.block().location());
        world.setChunkForceLoaded(event.block().location(), false);
        runtime.registry.unregister(event.block().location());
    }
}

