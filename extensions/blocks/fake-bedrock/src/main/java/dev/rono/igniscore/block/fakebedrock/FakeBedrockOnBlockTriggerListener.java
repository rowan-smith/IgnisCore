package dev.rono.igniscore.block.fakebedrock;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class FakeBedrockOnBlockTriggerListener implements OnBlockTriggerListener {
    private final FakeBedrockRuntime runtime;

    FakeBedrockOnBlockTriggerListener(FakeBedrockRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
