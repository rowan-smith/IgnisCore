package dev.rono.igniscore.block.invisiwall;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class InvisiwallRuntime {
    final IgnisStrategyContext context;
    final InvisiwallBehavior behavior;

    InvisiwallRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new InvisiwallBehavior(context);
    }
}
