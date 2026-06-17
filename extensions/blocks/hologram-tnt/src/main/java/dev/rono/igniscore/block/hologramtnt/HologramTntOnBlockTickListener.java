package dev.rono.igniscore.block.hologramtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class HologramTntOnBlockTickListener implements OnBlockTickListener {
    private final HologramTntRuntime runtime;

    HologramTntOnBlockTickListener(HologramTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
