package dev.rono.igniscore.block.compostteabrewer;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CompostTeaBrewerOnBlockClickListener());
        CompostTeaBrewerRuntime runtime = new CompostTeaBrewerRuntime(context);
        context.eventBus().subscribe(new CompostTeaBrewerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new CompostTeaBrewerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new CompostTeaBrewerOnBlockInteractListener(runtime));
    }

}
