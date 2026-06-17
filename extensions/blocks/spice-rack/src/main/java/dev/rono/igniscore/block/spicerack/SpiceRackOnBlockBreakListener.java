package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.spicerack.SpiceRackSupport;

final class SpiceRackOnBlockBreakListener implements OnBlockBreakListener {
    private final SpiceRackRuntime runtime;

    SpiceRackOnBlockBreakListener(SpiceRackRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.unregister(event.block().location());
    }
}

