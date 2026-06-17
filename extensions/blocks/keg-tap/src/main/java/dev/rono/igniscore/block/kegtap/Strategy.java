package dev.rono.igniscore.block.kegtap;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new KegTapOnBlockClickListener());
        KegTapRuntime runtime = new KegTapRuntime(context);
        context.eventBus().subscribe(new KegTapOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockInteractListener(runtime));
    }

}
