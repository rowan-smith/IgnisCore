package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class AutoSieveOnBlockBreakListener implements OnBlockBreakListener {
    private final AutoSieveBehavior behavior;

    AutoSieveOnBlockBreakListener(AutoSieveBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        behavior.onPlacedBreak(event.definition(), event.block());
    }
}
