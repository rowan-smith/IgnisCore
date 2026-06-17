package dev.rono.igniscore.block.wildfireseed;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class WildfireSeedOnBlockTriggerListener implements OnBlockTriggerListener {
    private final WildfireSeedRuntime runtime;

    WildfireSeedOnBlockTriggerListener(WildfireSeedRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
