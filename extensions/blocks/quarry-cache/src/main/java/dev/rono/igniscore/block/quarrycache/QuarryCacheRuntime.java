package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class QuarryCacheRuntime {
    final QuarryCacheRegistry registry;

    QuarryCacheRuntime(IgnisStrategyContext context) {
        this.registry = new QuarryCacheRegistry(context);
    }
}

