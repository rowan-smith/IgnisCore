package dev.rono.igniscore.block.tsunamicharge;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class TsunamiChargeOnBlockTickListener implements OnBlockTickListener {
    private final TsunamiChargeRuntime runtime;

    TsunamiChargeOnBlockTickListener(TsunamiChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
