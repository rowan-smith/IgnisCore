package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new LostAndFoundBinOnBlockClickListener());
        LostAndFoundBinRuntime runtime = new LostAndFoundBinRuntime(context);
        context.eventBus().subscribe(new LostAndFoundBinOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockInteractListener(runtime));
    }

}
