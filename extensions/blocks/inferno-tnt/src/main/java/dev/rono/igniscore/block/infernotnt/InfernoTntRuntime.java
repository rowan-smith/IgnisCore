package dev.rono.igniscore.block.infernotnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class InfernoTntRuntime {
    final IgnisStrategyContext context;
    final InfernoTntBehavior behavior;

    InfernoTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new InfernoTntBehavior(context);
    }
}
