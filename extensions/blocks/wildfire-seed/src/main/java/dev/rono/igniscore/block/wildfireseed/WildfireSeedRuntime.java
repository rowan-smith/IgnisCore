package dev.rono.igniscore.block.wildfireseed;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class WildfireSeedRuntime {
    final IgnisStrategyContext context;
    final WildfireSeedBehavior behavior;

    WildfireSeedRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new WildfireSeedBehavior(context);
    }
}
