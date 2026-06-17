package dev.rono.igniscore.block.smokerstack;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SmokerStackOnBlockClickListener());
        context.eventBus().subscribe(new SmokerStackOnBlockPlaceListener(context));
        context.eventBus().subscribe(new SmokerStackOnBlockBreakListener());
    }

}
