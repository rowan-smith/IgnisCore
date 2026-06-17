package dev.rono.igniscore.block.concussiontnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class ConcussionOnBlockTickListener implements OnBlockTickListener {
    private final ConcussionRuntime runtime;

    ConcussionOnBlockTickListener(ConcussionRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
