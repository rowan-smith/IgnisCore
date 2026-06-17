package dev.rono.igniscore.block.solarflaretnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class SolarFlareTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final SolarFlareTntRuntime runtime;

    SolarFlareTntOnBlockTriggerListener(SolarFlareTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
