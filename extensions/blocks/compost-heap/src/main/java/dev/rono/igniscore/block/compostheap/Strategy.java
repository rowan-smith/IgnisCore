package dev.rono.igniscore.block.compostheap;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CompostHeapOnBlockClickListener());
        context.eventBus().subscribe(new CompostHeapOnBlockPlaceListener(context));
        context.eventBus().subscribe(new CompostHeapOnBlockBreakListener());
    }

}
