package dev.rono.igniscore.block.pollinatorblock;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PollinatorBlockOnBlockClickListener());
        context.eventBus().subscribe(new PollinatorBlockOnBlockPlaceListener(context));
        context.eventBus().subscribe(new PollinatorBlockOnBlockBreakListener());
    }

}
