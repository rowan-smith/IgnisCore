package dev.rono.igniscore.block.compostteabrewer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.compostteabrewer.CompostTeaBrewerSupport;

final class CompostTeaBrewerOnBlockBreakListener implements OnBlockBreakListener {
    private final CompostTeaBrewerRuntime runtime;

    CompostTeaBrewerOnBlockBreakListener(CompostTeaBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

