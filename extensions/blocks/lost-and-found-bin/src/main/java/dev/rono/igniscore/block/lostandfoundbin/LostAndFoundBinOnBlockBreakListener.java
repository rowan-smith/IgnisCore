package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.lostandfoundbin.LostAndFoundBinSupport;

final class LostAndFoundBinOnBlockBreakListener implements OnBlockBreakListener {
    private final LostAndFoundBinRuntime runtime;

    LostAndFoundBinOnBlockBreakListener(LostAndFoundBinRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

