package dev.rono.igniscore.block.solarflaretnt;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SolarFlareTntRuntime {
    final IgnisStrategyContext context;
    final SolarFlareTntBehavior behavior;

    SolarFlareTntRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new SolarFlareTntBehavior(context);
    }
}
