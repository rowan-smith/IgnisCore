package dev.rono.igniscore.block.trampolinetnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class TrampolineTntOnBlockTickListener implements OnBlockTickListener {
    private final TrampolineTntRuntime runtime;

    TrampolineTntOnBlockTickListener(TrampolineTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
