package dev.rono.igniscore.block.pizzaoven;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PizzaOvenRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    PizzaOvenRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "pizza-oven");
    }
}

