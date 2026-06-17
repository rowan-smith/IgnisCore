package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new QuarryCacheOnBlockClickListener());
        QuarryCacheRuntime runtime = new QuarryCacheRuntime(context);
        context.eventBus().subscribe(new QuarryCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new QuarryCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new QuarryCacheOnBlockInteractListener(runtime));
    }
}
