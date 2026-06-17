package dev.rono.igniscore.block.securetradetable;

import dev.rono.extensions.shared.gui.SecureTradeRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SecureTradeTableRuntime {
    final IgnisStrategyContext context;
    final SecureTradeRegistry registry;

    SecureTradeTableRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.registry = new SecureTradeRegistry(context);
    }
}

