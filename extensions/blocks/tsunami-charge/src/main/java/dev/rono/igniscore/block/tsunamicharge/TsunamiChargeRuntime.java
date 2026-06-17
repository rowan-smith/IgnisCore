package dev.rono.igniscore.block.tsunamicharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class TsunamiChargeRuntime {
    final IgnisStrategyContext context;
    final TsunamiChargeBehavior behavior;

    TsunamiChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new TsunamiChargeBehavior(context);
    }
}
