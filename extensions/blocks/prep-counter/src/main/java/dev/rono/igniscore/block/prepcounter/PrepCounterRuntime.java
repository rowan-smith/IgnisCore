package dev.rono.igniscore.block.prepcounter;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PrepCounterRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    PrepCounterRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "prep-counter");
    }
}

