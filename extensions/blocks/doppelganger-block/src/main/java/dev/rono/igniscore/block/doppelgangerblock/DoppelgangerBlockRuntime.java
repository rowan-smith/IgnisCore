package dev.rono.igniscore.block.doppelgangerblock;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class DoppelgangerBlockRuntime {
    final IgnisStrategyContext context;
    final DoppelgangerBlockBehavior behavior;

    DoppelgangerBlockRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new DoppelgangerBlockBehavior(context);
    }
}
