package dev.rono.igniscore.block.displaycase;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class DisplayCaseRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    DisplayCaseRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "display-case");
    }
}

