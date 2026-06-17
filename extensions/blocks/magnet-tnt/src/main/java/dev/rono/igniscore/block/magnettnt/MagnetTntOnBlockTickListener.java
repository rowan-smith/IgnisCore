package dev.rono.igniscore.block.magnettnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class MagnetTntOnBlockTickListener implements OnBlockTickListener {
    private final MagnetTntRuntime runtime;

    MagnetTntOnBlockTickListener(MagnetTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
