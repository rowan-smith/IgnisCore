package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class DepthChargeRuntime {
    final IgnisStrategyContext context;
    final DepthChargeBehavior behavior;

    DepthChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new DepthChargeBehavior(context);
    }
}
