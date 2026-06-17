package dev.rono.igniscore.block.orbittnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class OrbitTntRuntime {
    final IgnisStrategyContext context;
    final OrbitTntBehavior behavior;

    OrbitTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new OrbitTntBehavior(context);
    }
}
