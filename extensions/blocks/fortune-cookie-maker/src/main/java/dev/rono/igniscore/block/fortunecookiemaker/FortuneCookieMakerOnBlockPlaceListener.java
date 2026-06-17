package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class FortuneCookieMakerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final FortuneCookieMakerRuntime runtime;

    FortuneCookieMakerOnBlockPlaceListener(FortuneCookieMakerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), FortuneCookieMakerSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 40),
                () -> FortuneCookieMakerSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

