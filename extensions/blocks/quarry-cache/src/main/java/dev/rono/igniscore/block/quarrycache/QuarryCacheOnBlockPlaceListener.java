package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class QuarryCacheOnBlockPlaceListener implements OnBlockPlaceListener {
    private final QuarryCacheRuntime runtime;

    QuarryCacheOnBlockPlaceListener(QuarryCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.register(event.block().location(), event.definition(), event.placedFrom());
    }
}

