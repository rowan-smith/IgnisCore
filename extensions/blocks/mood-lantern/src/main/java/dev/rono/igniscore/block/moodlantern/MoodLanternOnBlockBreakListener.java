package dev.rono.igniscore.block.moodlantern;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.moodlantern.MoodLanternSupport;

final class MoodLanternOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
    }
}

