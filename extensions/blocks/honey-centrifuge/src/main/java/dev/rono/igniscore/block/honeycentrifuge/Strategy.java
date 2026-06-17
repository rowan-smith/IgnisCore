package dev.rono.igniscore.block.honeycentrifuge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new HoneyCentrifugeOnBlockClickListener());
        context.eventBus().subscribe(new HoneyCentrifugeOnBlockPlaceListener(context));
        context.eventBus().subscribe(new HoneyCentrifugeOnBlockBreakListener());
    }

}
