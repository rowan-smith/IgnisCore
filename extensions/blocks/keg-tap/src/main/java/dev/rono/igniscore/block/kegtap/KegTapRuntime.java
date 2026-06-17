package dev.rono.igniscore.block.kegtap;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class KegTapRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    KegTapRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "keg-tap");
    }
}

