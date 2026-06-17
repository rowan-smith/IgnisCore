package dev.rono.igniscore.block.sprinklerhead;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SprinklerHeadOnBlockClickListener());
        context.eventBus().subscribe(new SprinklerHeadOnBlockPlaceListener(context));
        context.eventBus().subscribe(new SprinklerHeadOnBlockBreakListener());
    }

}
