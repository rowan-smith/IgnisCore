package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ShrapnelMineRuntime {
    final IgnisStrategyContext context;
    final ShrapnelMineBehavior behavior;

    ShrapnelMineRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new ShrapnelMineBehavior(context);
    }
}
