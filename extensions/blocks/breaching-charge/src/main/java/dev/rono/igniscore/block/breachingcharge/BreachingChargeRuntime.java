package dev.rono.igniscore.block.breachingcharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class BreachingChargeRuntime {
    final IgnisStrategyContext context;
    final BreachingChargeBehavior behavior;

    BreachingChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new BreachingChargeBehavior(context);
    }
}
