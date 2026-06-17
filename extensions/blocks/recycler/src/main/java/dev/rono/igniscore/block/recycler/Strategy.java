package dev.rono.igniscore.block.recycler;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new RecyclerOnBlockClickListener());
        context.eventBus().subscribe(new RecyclerOnBlockPlaceListener(context));
        context.eventBus().subscribe(new RecyclerOnBlockBreakListener());
    }

}
