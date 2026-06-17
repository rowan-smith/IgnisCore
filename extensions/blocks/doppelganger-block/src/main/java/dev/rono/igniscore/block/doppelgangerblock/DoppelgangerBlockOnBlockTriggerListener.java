package dev.rono.igniscore.block.doppelgangerblock;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class DoppelgangerBlockOnBlockTriggerListener implements OnBlockTriggerListener {
    private final DoppelgangerBlockRuntime runtime;

    DoppelgangerBlockOnBlockTriggerListener(DoppelgangerBlockRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
