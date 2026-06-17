package dev.rono.igniscore.block.oresniffer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.oresniffer.OreSnifferSupport;

final class OreSnifferOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
    }
}

