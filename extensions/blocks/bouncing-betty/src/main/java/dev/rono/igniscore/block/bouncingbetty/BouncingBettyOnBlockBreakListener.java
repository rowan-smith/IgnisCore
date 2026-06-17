package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class BouncingBettyOnBlockBreakListener implements OnBlockBreakListener {
    private final BouncingBettyRuntime runtime;

    BouncingBettyOnBlockBreakListener(BouncingBettyRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.behavior.onPlacedBreak(event.block().location());
    }
}
