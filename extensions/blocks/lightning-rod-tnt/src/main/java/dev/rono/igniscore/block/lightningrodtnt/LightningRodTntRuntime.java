package dev.rono.igniscore.block.lightningrodtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class LightningRodTntRuntime {
    final IgnisStrategyContext context;
    final LightningRodTntBehavior behavior;

    LightningRodTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new LightningRodTntBehavior(context);
    }
}
