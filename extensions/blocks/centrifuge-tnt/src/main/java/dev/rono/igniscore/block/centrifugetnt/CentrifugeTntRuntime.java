package dev.rono.igniscore.block.centrifugetnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class CentrifugeTntRuntime {
    final IgnisStrategyContext context;
    final CentrifugeTntBehavior behavior;

    CentrifugeTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new CentrifugeTntBehavior(context);
    }
}
