package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.fortunecookiemaker.FortuneCookieMakerSupport;

final class FortuneCookieMakerOnBlockBreakListener implements OnBlockBreakListener {
    private final FortuneCookieMakerRuntime runtime;

    FortuneCookieMakerOnBlockBreakListener(FortuneCookieMakerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

