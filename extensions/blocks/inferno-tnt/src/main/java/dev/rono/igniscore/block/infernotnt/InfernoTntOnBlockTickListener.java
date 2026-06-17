package dev.rono.igniscore.block.infernotnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class InfernoTntOnBlockTickListener implements OnBlockTickListener {
    private final InfernoTntRuntime runtime;

    InfernoTntOnBlockTickListener(InfernoTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
