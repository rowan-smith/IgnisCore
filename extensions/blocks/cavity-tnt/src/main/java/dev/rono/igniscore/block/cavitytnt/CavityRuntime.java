package dev.rono.igniscore.block.cavitytnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class CavityRuntime {
    final IgnisStrategyContext context;
    final CavityBehavior behavior;

    CavityRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new CavityBehavior(context);
    }
}
