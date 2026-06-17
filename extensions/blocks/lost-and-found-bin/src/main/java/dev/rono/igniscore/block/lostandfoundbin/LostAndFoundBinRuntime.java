package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class LostAndFoundBinRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;
    int sweepCounter;

    LostAndFoundBinRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "lost-and-found-bin");
    }
}

