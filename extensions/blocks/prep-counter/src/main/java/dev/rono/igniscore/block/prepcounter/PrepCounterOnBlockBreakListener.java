package dev.rono.igniscore.block.prepcounter;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.prepcounter.PrepCounterSupport;

final class PrepCounterOnBlockBreakListener implements OnBlockBreakListener {
    private final PrepCounterRuntime runtime;

    PrepCounterOnBlockBreakListener(PrepCounterRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

