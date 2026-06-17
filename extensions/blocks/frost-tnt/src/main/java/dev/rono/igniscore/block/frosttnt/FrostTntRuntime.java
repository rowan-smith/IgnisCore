package dev.rono.igniscore.block.frosttnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FrostTntRuntime {
    final IgnisStrategyContext context;
    final FrostTntBehavior behavior;

    FrostTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new FrostTntBehavior(context);
    }
}
