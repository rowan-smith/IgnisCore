package dev.rono.igniscore.block.breachingcharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class BreachingChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final BreachingChargeRuntime runtime;

    BreachingChargeOnBlockTriggerListener(BreachingChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
