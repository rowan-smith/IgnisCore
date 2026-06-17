package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class SeismicOnBlockTickListener implements OnBlockTickListener {
    private final SeismicRuntime runtime;

    SeismicOnBlockTickListener(SeismicRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
