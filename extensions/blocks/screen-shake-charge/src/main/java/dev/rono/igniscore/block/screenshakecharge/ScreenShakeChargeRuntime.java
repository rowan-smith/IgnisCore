package dev.rono.igniscore.block.screenshakecharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ScreenShakeChargeRuntime {
    final IgnisStrategyContext context;
    final ScreenShakeChargeBehavior behavior;

    ScreenShakeChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new ScreenShakeChargeBehavior(context);
    }
}
