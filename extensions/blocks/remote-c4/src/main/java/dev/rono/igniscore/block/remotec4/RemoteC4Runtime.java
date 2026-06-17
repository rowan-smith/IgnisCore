package dev.rono.igniscore.block.remotec4;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class RemoteC4Runtime {
    final IgnisStrategyContext context;
    final RemoteC4Behavior behavior;

    RemoteC4Runtime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new RemoteC4Behavior(context);
    }
}
