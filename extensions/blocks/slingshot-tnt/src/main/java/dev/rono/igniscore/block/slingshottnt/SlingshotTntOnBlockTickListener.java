package dev.rono.igniscore.block.slingshottnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class SlingshotTntOnBlockTickListener implements OnBlockTickListener {
    private final SlingshotTntRuntime runtime;

    SlingshotTntOnBlockTickListener(SlingshotTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
