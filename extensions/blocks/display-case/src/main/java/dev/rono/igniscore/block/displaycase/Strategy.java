package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new DisplayCaseOnBlockClickListener());
        DisplayCaseRuntime runtime = new DisplayCaseRuntime(context);
        context.eventBus().subscribe(new DisplayCaseOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockInteractListener(runtime));
    }

}
