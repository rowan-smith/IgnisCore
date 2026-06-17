package dev.rono.igniscore.block.featherfallcharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FeatherfallChargeRuntime {
    final IgnisStrategyContext context;
    final FeatherfallChargeBehavior behavior;

    FeatherfallChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new FeatherfallChargeBehavior(context);
    }
}
