package dev.rono.igniscore.block.antigravityzone;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class AntiGravityZoneRuntime {
    final IgnisStrategyContext context;
    final AntiGravityZoneBehavior behavior;

    AntiGravityZoneRuntime(IgnisStrategyContext context) {
        this.context = context;
        this.behavior = new AntiGravityZoneBehavior(context);
    }
}
