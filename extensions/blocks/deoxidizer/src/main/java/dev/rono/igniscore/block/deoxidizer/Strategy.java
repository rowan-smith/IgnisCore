package dev.rono.igniscore.block.deoxidizer;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new DeoxidizerOnBlockClickListener());
        context.eventBus().subscribe(new DeoxidizerOnBlockPlaceListener(context));
        context.eventBus().subscribe(new DeoxidizerOnBlockBreakListener());
    }

}
