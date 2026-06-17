package dev.rono.igniscore.block.ringtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class RingRuntime {
    final IgnisStrategyContext context;
    final RingBehavior behavior;

    RingRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new RingBehavior(context);
    }
}
