package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class DepthChargeOnBlockTickListener implements OnBlockTickListener {
    private final DepthChargeRuntime runtime;

    DepthChargeOnBlockTickListener(DepthChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
