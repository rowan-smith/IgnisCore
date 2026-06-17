package dev.rono.igniscore.block.mudslidetnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class MudslideTntRuntime {
    final IgnisStrategyContext context;
    final MudslideTntBehavior behavior;

    MudslideTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new MudslideTntBehavior(context);
    }
}
