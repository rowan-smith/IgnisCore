package dev.rono.igniscore.block.echoblast;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class EchoBlastRuntime {
    final IgnisStrategyContext context;
    final EchoBlastBehavior behavior;

    EchoBlastRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new EchoBlastBehavior(context);
    }
}
