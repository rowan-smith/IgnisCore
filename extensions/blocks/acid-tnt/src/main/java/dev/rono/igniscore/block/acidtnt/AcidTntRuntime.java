package dev.rono.igniscore.block.acidtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class AcidTntRuntime {
    final IgnisStrategyContext context;
    final AcidTntBehavior behavior;

    AcidTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new AcidTntBehavior(context);
    }
}
