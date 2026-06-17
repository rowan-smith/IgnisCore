package dev.rono.igniscore.block.timedcharge;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class TimedChargeOnBlockTickListener implements OnBlockTickListener {
    private final TimedChargeRuntime runtime;

    TimedChargeOnBlockTickListener(TimedChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
