package dev.rono.igniscore.block.ringtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class RingOnBlockTickListener implements OnBlockTickListener {
    private final RingRuntime runtime;

    RingOnBlockTickListener(RingRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
