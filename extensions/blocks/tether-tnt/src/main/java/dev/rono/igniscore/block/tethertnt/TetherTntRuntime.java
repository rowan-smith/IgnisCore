package dev.rono.igniscore.block.tethertnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class TetherTntRuntime {
    final IgnisStrategyContext context;
    final TetherTntBehavior behavior;

    TetherTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new TetherTntBehavior(context);
    }
}
