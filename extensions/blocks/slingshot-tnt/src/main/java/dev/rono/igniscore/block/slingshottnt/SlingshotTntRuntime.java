package dev.rono.igniscore.block.slingshottnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SlingshotTntRuntime {
    final IgnisStrategyContext context;
    final SlingshotTntBehavior behavior;

    SlingshotTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new SlingshotTntBehavior(context);
    }
}
