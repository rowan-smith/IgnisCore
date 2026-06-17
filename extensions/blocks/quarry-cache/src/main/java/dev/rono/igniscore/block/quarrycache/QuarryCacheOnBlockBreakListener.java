package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class QuarryCacheOnBlockBreakListener implements OnBlockBreakListener {
    private final QuarryCacheRuntime runtime;

    QuarryCacheOnBlockBreakListener(QuarryCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.handleBreak(event.block().location(), event.droppedItem());
    }
}

