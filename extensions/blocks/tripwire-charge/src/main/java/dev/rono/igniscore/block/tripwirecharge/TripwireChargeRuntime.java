package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class TripwireChargeRuntime {
    final IgnisStrategyContext context;
    final TripwireChargeBehavior behavior;

    TripwireChargeRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new TripwireChargeBehavior(context);
    }
}
