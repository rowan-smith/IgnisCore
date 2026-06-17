package dev.rono.igniscore.block.ringtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class RingOnBlockTriggerListener implements OnBlockTriggerListener {
    private final RingRuntime runtime;

    RingOnBlockTriggerListener(RingRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
