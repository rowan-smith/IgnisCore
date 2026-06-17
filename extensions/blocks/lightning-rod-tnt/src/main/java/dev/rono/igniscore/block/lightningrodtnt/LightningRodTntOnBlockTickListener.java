package dev.rono.igniscore.block.lightningrodtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class LightningRodTntOnBlockTickListener implements OnBlockTickListener {
    private final LightningRodTntRuntime runtime;

    LightningRodTntOnBlockTickListener(LightningRodTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
