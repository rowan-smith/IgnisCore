package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class CoffeeBrewerRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    CoffeeBrewerRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "coffee-brewer");
    }
}

