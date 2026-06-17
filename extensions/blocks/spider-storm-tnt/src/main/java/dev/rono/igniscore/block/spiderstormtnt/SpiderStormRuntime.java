package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SpiderStormRuntime {
    final IgnisStrategyContext context;
    final IgnisNbtService nbtService;

    SpiderStormRuntime(IgnisStrategyContext context) {
        this.context = context;
                this.nbtService = context.nbt();
    }
}

