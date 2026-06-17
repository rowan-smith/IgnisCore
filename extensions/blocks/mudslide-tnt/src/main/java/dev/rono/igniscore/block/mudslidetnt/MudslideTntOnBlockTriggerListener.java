package dev.rono.igniscore.block.mudslidetnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class MudslideTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final MudslideTntRuntime runtime;

    MudslideTntOnBlockTriggerListener(MudslideTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
