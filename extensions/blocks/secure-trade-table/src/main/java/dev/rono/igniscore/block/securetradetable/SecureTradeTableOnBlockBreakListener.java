package dev.rono.igniscore.block.securetradetable;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.securetradetable.SecureTradeTableSupport;

final class SecureTradeTableOnBlockBreakListener implements OnBlockBreakListener {
    private final SecureTradeTableRuntime runtime;

    SecureTradeTableOnBlockBreakListener(SecureTradeTableRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.unregister(event.block().location());
    }
}

