package dev.rono.igniscore.block.saplingnursery;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SaplingNurseryRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    SaplingNurseryRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "sapling-nursery");
    }
}

