package dev.rono.igniscore.block.stasisfield;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new StasisFieldOnBlockClickListener());
        context.eventBus().subscribe(new StasisFieldOnBlockPlaceListener(context));
        context.eventBus().subscribe(new StasisFieldOnBlockBreakListener());
    }

}
