package dev.rono.igniscore.block.mosscreeper;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MossCreeperOnBlockClickListener());
        context.eventBus().subscribe(new MossCreeperOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MossCreeperOnBlockBreakListener());
    }

}
