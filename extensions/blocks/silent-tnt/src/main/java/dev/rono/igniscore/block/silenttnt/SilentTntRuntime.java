package dev.rono.igniscore.block.silenttnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SilentTntRuntime {
    final IgnisStrategyContext context;
    final SilentTntBehavior behavior;

    SilentTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new SilentTntBehavior(context);
    }
}
