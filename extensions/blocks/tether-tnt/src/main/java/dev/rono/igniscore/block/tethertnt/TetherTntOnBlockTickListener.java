package dev.rono.igniscore.block.tethertnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class TetherTntOnBlockTickListener implements OnBlockTickListener {
    private final TetherTntRuntime runtime;

    TetherTntOnBlockTickListener(TetherTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
