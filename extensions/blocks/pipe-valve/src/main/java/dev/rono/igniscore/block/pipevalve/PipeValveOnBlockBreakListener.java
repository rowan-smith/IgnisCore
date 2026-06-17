package dev.rono.igniscore.block.pipevalve;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class PipeValveOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        LinkedBlockRegistry.unregister(event.block().location());
        PipeValveSupport.OPEN.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}

