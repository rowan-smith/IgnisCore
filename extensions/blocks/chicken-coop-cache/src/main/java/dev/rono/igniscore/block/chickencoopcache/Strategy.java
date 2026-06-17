package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockClickListener());
        ChickenCoopCacheRuntime runtime = new ChickenCoopCacheRuntime(context);
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockInteractListener(runtime));
    }

}
