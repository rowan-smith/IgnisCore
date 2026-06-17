package dev.rono.igniscore.block.silenttnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class SilentTntOnBlockTickListener implements OnBlockTickListener {
    private final SilentTntRuntime runtime;

    SilentTntOnBlockTickListener(SilentTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
