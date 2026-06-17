package dev.rono.igniscore.block.kegtap;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.kegtap.KegTapSupport;

final class KegTapOnBlockBreakListener implements OnBlockBreakListener {
    private final KegTapRuntime runtime;

    KegTapOnBlockBreakListener(KegTapRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

