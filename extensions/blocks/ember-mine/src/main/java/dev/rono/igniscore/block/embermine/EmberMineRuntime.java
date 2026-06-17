package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class EmberMineRuntime {
    final IgnisStrategyContext context;
    final EmberMineBehavior behavior;

    EmberMineRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new EmberMineBehavior(context);
    }
}
