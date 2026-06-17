package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class IceCreamFreezerRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    IceCreamFreezerRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "ice-cream-freezer");
    }
}

