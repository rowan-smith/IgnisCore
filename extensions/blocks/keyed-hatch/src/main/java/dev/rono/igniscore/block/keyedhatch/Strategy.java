package dev.rono.igniscore.block.keyedhatch;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new KeyedHatchOnBlockClickListener());
        context.eventBus().subscribe(new KeyedHatchOnBlockPlaceListener(context));
        context.eventBus().subscribe(new KeyedHatchOnBlockBreakListener());
    }

}
