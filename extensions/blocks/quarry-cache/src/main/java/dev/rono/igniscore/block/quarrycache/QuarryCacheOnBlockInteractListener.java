package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;

final class QuarryCacheOnBlockInteractListener implements OnBlockInteractListener {
    private final QuarryCacheRuntime runtime;

    QuarryCacheOnBlockInteractListener(QuarryCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openGui(event.player(), event.block().location());
        }
    }
}

