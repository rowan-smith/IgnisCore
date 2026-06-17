package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class IrrigationSprinklerRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    IrrigationSprinklerRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "irrigation-sprinkler");
    }
}

