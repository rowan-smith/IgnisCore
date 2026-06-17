package dev.rono.igniscore.block.moodlantern;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class MoodLanternOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    MoodLanternOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 25),
                () -> MoodLanternSupport.tick(context, event.block().definition(), event.block().location()));
    }
}

