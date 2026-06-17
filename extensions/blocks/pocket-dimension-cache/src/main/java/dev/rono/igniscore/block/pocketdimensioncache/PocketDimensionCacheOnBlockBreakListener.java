package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.pocketdimensioncache.PocketDimensionCacheSupport;

final class PocketDimensionCacheOnBlockBreakListener implements OnBlockBreakListener {
    private final PocketDimensionCacheRuntime runtime;

    PocketDimensionCacheOnBlockBreakListener(PocketDimensionCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.unregister(event.block().location());
    }
}

