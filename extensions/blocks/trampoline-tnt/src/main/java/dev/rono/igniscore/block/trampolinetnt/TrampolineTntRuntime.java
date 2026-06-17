package dev.rono.igniscore.block.trampolinetnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class TrampolineTntRuntime {
    final IgnisStrategyContext context;
    final TrampolineTntBehavior behavior;

    TrampolineTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new TrampolineTntBehavior(context);
    }
}
