package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ChickenCoopCacheRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    ChickenCoopCacheRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "chicken-coop-cache");
    }
}

