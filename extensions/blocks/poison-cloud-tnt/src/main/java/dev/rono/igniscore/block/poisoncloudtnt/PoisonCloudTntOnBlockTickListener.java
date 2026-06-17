package dev.rono.igniscore.block.poisoncloudtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class PoisonCloudTntOnBlockTickListener implements OnBlockTickListener {
    private final PoisonCloudTntRuntime runtime;

    PoisonCloudTntOnBlockTickListener(PoisonCloudTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
