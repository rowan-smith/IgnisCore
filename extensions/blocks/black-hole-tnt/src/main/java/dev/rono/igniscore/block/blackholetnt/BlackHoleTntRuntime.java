package dev.rono.igniscore.block.blackholetnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class BlackHoleTntRuntime {
    final IgnisStrategyContext context;
    final BlackHoleTntBehavior behavior;

    BlackHoleTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new BlackHoleTntBehavior(context);
    }
}
