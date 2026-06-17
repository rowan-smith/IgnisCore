package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class ClaymoreMineOnBlockBreakListener implements OnBlockBreakListener {
    private final ClaymoreMineRuntime runtime;

    ClaymoreMineOnBlockBreakListener(ClaymoreMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.behavior.onPlacedBreak(event.block().location());
    }
}
