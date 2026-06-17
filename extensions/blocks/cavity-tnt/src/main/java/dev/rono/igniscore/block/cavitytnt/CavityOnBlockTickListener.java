package dev.rono.igniscore.block.cavitytnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class CavityOnBlockTickListener implements OnBlockTickListener {
    private final CavityRuntime runtime;

    CavityOnBlockTickListener(CavityRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
