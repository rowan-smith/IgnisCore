package dev.rono.igniscore.block.concussiontnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ConcussionRuntime {
    final IgnisStrategyContext context;
    final ConcussionBehavior behavior;

    ConcussionRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new ConcussionBehavior(context);
    }
}
