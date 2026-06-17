package dev.rono.igniscore.block.scarecharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ScareChargeRuntime {
    final IgnisStrategyContext context;
    final ScareChargeBehavior behavior;

    ScareChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new ScareChargeBehavior(context);
    }
}
