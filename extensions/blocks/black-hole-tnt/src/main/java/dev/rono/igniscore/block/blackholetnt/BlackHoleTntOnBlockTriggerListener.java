package dev.rono.igniscore.block.blackholetnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class BlackHoleTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final BlackHoleTntRuntime runtime;

    BlackHoleTntOnBlockTriggerListener(BlackHoleTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
