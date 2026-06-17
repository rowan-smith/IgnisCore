package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;

final class AutoSieveOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
    }
}
