package dev.rono.igniscore.block.glitchtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class GlitchTntRuntime {
    final IgnisStrategyContext context;
    final GlitchTntBehavior behavior;

    GlitchTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new GlitchTntBehavior(context);
    }
}
