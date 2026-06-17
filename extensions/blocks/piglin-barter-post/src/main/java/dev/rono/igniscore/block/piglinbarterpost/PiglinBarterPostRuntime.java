package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PiglinBarterPostRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    PiglinBarterPostRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "piglin-barter-post");
    }
}

