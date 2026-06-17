package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class DepthChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final DepthChargeRuntime runtime;

    DepthChargeOnBlockTriggerListener(DepthChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
