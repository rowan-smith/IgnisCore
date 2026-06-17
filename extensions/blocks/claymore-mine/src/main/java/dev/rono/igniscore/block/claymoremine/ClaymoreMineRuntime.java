package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class ClaymoreMineRuntime {
    final IgnisStrategyContext context;
    final ClaymoreMineBehavior behavior;

    ClaymoreMineRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new ClaymoreMineBehavior(context);
    }
}
