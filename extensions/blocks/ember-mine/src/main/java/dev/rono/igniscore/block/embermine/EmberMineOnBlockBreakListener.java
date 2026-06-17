package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class EmberMineOnBlockBreakListener implements OnBlockBreakListener {
    private final EmberMineRuntime runtime;

    EmberMineOnBlockBreakListener(EmberMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.behavior.onPlacedBreak(event.block().location());
    }
}
