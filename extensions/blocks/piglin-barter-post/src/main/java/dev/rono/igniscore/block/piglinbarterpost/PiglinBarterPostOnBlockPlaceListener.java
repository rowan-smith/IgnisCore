package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class PiglinBarterPostOnBlockPlaceListener implements OnBlockPlaceListener {
    private final PiglinBarterPostRuntime runtime;

    PiglinBarterPostOnBlockPlaceListener(PiglinBarterPostRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), PiglinBarterPostSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 30),
                () -> PiglinBarterPostSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

