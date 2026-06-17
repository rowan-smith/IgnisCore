package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class ShrapnelMineOnBlockBreakListener implements OnBlockBreakListener {
    private final ShrapnelMineRuntime runtime;

    ShrapnelMineOnBlockBreakListener(ShrapnelMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.behavior.onPlacedBreak(event.block().location());
    }
}
