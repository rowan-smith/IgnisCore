package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ChunkLoaderLiteRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    ChunkLoaderLiteRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "chunk-loader-lite");
    }
}

