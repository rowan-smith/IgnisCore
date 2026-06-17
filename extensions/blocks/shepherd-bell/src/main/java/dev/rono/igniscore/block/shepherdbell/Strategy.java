package dev.rono.igniscore.block.shepherdbell;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ShepherdBellOnBlockClickListener());
        context.eventBus().subscribe(new ShepherdBellOnBlockPlaceListener(context));
        context.eventBus().subscribe(new ShepherdBellOnBlockBreakListener());
    }

}
