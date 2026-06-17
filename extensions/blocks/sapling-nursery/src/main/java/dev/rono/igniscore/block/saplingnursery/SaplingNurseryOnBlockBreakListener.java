package dev.rono.igniscore.block.saplingnursery;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.saplingnursery.SaplingNurserySupport;

final class SaplingNurseryOnBlockBreakListener implements OnBlockBreakListener {
    private final SaplingNurseryRuntime runtime;

    SaplingNurseryOnBlockBreakListener(SaplingNurseryRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

