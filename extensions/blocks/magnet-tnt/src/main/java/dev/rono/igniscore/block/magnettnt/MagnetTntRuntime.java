package dev.rono.igniscore.block.magnettnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class MagnetTntRuntime {
    final IgnisStrategyContext context;
    final MagnetTntBehavior behavior;

    MagnetTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new MagnetTntBehavior(context);
    }
}
