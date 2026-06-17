package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.chickencoopcache.ChickenCoopCacheSupport;

final class ChickenCoopCacheOnBlockBreakListener implements OnBlockBreakListener {
    private final ChickenCoopCacheRuntime runtime;

    ChickenCoopCacheOnBlockBreakListener(ChickenCoopCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.context.extensions().unregisterDropCollector(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

