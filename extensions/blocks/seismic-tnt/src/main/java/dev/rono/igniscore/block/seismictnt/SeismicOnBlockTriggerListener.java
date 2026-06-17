package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class SeismicOnBlockTriggerListener implements OnBlockTriggerListener {
    private final SeismicRuntime runtime;

    SeismicOnBlockTriggerListener(SeismicRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
