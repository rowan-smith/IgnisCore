package dev.rono.igniscore.block.keyedhatch;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class KeyedHatchOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        LinkedBlockRegistry.unregister(event.block().location());
        KeyedHatchSupport.OPEN.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}

