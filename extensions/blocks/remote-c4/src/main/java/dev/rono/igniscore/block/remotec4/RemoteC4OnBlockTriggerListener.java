package dev.rono.igniscore.block.remotec4;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class RemoteC4OnBlockTriggerListener implements OnBlockTriggerListener {
    private final RemoteC4Runtime runtime;

    RemoteC4OnBlockTriggerListener(RemoteC4Runtime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
