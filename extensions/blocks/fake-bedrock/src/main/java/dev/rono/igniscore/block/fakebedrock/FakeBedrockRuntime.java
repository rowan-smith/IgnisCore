package dev.rono.igniscore.block.fakebedrock;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FakeBedrockRuntime {
    final IgnisStrategyContext context;
    final FakeBedrockBehavior behavior;

    FakeBedrockRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new FakeBedrockBehavior(context);
    }
}
