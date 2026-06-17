package dev.rono.igniscore.block.centrifugetnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class CentrifugeTntOnBlockTickListener implements OnBlockTickListener {
    private final CentrifugeTntRuntime runtime;

    CentrifugeTntOnBlockTickListener(CentrifugeTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
