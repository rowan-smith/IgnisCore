package dev.rono.igniscore.block.displaycase;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        DisplayCaseRuntime runtime = new DisplayCaseRuntime(context);
        context.eventBus().subscribe(new DisplayCaseOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockInteractListener(runtime));
    }

}
