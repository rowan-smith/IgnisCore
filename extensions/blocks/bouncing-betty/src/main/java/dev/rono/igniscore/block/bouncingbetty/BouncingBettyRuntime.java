package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class BouncingBettyRuntime {
    final IgnisStrategyContext context;
    final BouncingBettyBehavior behavior;

    BouncingBettyRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new BouncingBettyBehavior(context);
    }
}
