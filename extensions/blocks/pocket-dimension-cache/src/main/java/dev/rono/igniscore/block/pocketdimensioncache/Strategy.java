package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockClickListener());
        PocketDimensionCacheRuntime runtime = new PocketDimensionCacheRuntime(context);
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockInteractListener(runtime));
    }

}
