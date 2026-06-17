package dev.rono.igniscore.block.sprinklerhead;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class SprinklerHeadOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        LinkedBlockRegistry.unregister(event.block().location());
        SprinklerHeadSupport.ARMED.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}

