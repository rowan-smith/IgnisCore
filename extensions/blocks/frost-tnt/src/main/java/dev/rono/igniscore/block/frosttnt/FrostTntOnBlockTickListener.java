package dev.rono.igniscore.block.frosttnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class FrostTntOnBlockTickListener implements OnBlockTickListener {
    private final FrostTntRuntime runtime;

    FrostTntOnBlockTickListener(FrostTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
