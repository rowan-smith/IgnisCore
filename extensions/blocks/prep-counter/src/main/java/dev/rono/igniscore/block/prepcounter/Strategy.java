package dev.rono.igniscore.block.prepcounter;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        PrepCounterRuntime runtime = new PrepCounterRuntime(context);
        context.eventBus().subscribe(new PrepCounterOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PrepCounterOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PrepCounterOnBlockInteractListener(runtime));
    }

}
