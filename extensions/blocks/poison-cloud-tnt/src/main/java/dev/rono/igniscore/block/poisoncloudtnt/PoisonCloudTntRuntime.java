package dev.rono.igniscore.block.poisoncloudtnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PoisonCloudTntRuntime {
    final IgnisStrategyContext context;
    final PoisonCloudTntBehavior behavior;

    PoisonCloudTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new PoisonCloudTntBehavior(context);
    }
}
