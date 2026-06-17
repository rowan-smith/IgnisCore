package dev.rono.igniscore.block.miragetnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class MirageTntRuntime {
    final IgnisStrategyContext context;
    final MirageTntBehavior behavior;

    MirageTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new MirageTntBehavior(context);
    }
}
