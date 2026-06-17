package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class BouncingBettyOnBlockTriggerListener implements OnBlockTriggerListener {
    private final BouncingBettyRuntime runtime;

    BouncingBettyOnBlockTriggerListener(BouncingBettyRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
