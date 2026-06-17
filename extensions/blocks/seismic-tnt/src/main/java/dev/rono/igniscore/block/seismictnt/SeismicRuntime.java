package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SeismicRuntime {
    final IgnisStrategyContext context;
    final SeismicBehavior behavior;

    SeismicRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new SeismicBehavior(context);
    }
}
