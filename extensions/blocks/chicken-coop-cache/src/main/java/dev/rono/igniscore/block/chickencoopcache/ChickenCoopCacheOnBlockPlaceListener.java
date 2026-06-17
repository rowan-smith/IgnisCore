package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class ChickenCoopCacheOnBlockPlaceListener implements OnBlockPlaceListener {
    private final ChickenCoopCacheRuntime runtime;

    ChickenCoopCacheOnBlockPlaceListener(ChickenCoopCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), ChickenCoopCacheSupport.title(runtime, event.block().definition()), 3);
        runtime.context.extensions().registerDropCollector(event.block().location(), (breakLocation, drops) -> ChickenCoopCacheSupport.collectEggs(runtime, event.block().location(), drops));
        long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 100);
        PlacedTickSupport.start(runtime.context, event.block().location(), period, () -> ChickenCoopCacheSupport.tick(runtime, event.block().definition(), event.block().location()));
        TheatricsSupport.chime(ChickenCoopCacheSupport.worldAt(runtime, event.block().location()), Locations.toCenter(event.block().location()), 1.0f);
    }
}

