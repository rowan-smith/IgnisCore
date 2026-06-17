package dev.rono.igniscore.block.spicerack;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SpiceRackRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    SpiceRackRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "spice-rack");
    }
}

