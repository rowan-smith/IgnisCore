package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.piglinbarterpost.PiglinBarterPostSupport;

final class PiglinBarterPostOnBlockBreakListener implements OnBlockBreakListener {
    private final PiglinBarterPostRuntime runtime;

    PiglinBarterPostOnBlockBreakListener(PiglinBarterPostRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

