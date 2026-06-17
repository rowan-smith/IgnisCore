package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PocketDimensionCacheRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    PocketDimensionCacheRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "pocket-dimension-cache");
    }
}

