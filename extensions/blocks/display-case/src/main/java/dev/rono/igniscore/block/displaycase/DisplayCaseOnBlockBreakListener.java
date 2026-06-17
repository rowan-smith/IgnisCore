package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.displaycase.DisplayCaseSupport;

final class DisplayCaseOnBlockBreakListener implements OnBlockBreakListener {
    private final DisplayCaseRuntime runtime;

    DisplayCaseOnBlockBreakListener(DisplayCaseRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.unregister(event.block().location());
    }
}

