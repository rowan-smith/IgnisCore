package dev.rono.igniscore.block.concussiontnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class ConcussionOnBlockTriggerListener implements OnBlockTriggerListener {
    private final ConcussionRuntime runtime;

    ConcussionOnBlockTriggerListener(ConcussionRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
