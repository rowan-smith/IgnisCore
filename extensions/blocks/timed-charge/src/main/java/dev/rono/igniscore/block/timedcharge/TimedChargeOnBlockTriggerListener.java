package dev.rono.igniscore.block.timedcharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class TimedChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final TimedChargeRuntime runtime;

    TimedChargeOnBlockTriggerListener(TimedChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
