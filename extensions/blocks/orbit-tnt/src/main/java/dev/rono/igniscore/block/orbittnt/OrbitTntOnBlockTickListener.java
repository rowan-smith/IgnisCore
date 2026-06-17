package dev.rono.igniscore.block.orbittnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class OrbitTntOnBlockTickListener implements OnBlockTickListener {
    private final OrbitTntRuntime runtime;

    OrbitTntOnBlockTickListener(OrbitTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
