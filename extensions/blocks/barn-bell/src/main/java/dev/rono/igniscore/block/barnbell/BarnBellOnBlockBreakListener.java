package dev.rono.igniscore.block.barnbell;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class BarnBellOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        LinkedBlockRegistry.unregister(event.block().location());
        BarnBellSupport.COOLDOWN.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}

