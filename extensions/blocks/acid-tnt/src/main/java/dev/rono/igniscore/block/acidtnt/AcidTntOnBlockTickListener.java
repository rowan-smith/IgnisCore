package dev.rono.igniscore.block.acidtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class AcidTntOnBlockTickListener implements OnBlockTickListener {
    private final AcidTntRuntime runtime;

    AcidTntOnBlockTickListener(AcidTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
