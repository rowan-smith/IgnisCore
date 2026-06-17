package dev.rono.igniscore.block.barnbell;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new BarnBellOnBlockClickListener());
        context.eventBus().subscribe(new BarnBellOnBlockPlaceListener(context));
        context.eventBus().subscribe(new BarnBellOnBlockBreakListener());
    }

}
