package dev.rono.igniscore.block.hologramtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class HologramTntRuntime {
    final IgnisStrategyContext context;
    final HologramTntBehavior behavior;

    HologramTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new HologramTntBehavior(context);
    }
}
