package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FortuneCookieMakerRuntime {
    final IgnisStrategyContext context;
    final BlockStorageRegistry registry;

    FortuneCookieMakerRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new BlockStorageRegistry(context, "fortune-cookie-maker");
    }
}

