package dev.rono.igniscore.block.kegtap;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        KegTapRuntime runtime = new KegTapRuntime(context);
        context.eventBus().subscribe(new KegTapOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockInteractListener(runtime));
    }

}
