package dev.rono.igniscore.block.frosttnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class FrostTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final FrostTntRuntime runtime;

    FrostTntOnBlockTriggerListener(FrostTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
