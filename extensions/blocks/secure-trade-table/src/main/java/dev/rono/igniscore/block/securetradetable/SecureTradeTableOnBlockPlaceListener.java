package dev.rono.igniscore.block.securetradetable;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.util.Locations;

final class SecureTradeTableOnBlockPlaceListener implements OnBlockPlaceListener {
    private final SecureTradeTableRuntime runtime;

    SecureTradeTableOnBlockPlaceListener(SecureTradeTableRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.register(event.block().location(), SecureTradeTableSupport.title(runtime, event.block().definition()));
        TheatricsSupport.sparkle(SecureTradeTableSupport.worldAt(runtime, event.block().location()), Locations.toCenter(event.block().location()), "HAPPY_VILLAGER", 6);
    }
}

