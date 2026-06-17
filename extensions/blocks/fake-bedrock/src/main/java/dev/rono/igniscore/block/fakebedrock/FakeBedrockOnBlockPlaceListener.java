package dev.rono.igniscore.block.fakebedrock;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class FakeBedrockOnBlockPlaceListener implements OnBlockPlaceListener {
    private final FakeBedrockRuntime runtime;

    FakeBedrockOnBlockPlaceListener(FakeBedrockRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
