package dev.rono.igniscore.block.timedcharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class TimedChargeRuntime {
    final IgnisStrategyContext context;
    final TimedChargeBehavior behavior;

    TimedChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new TimedChargeBehavior(context);
    }
}
