package dev.rono.igniscore.block.drilltnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class DrillRuntime {
    final IgnisStrategyContext context;
    final DrillBehavior behavior;

    DrillRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new DrillBehavior(context);
    }
}
