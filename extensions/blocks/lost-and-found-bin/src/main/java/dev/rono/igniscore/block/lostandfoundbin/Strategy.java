package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        LostAndFoundBinRuntime runtime = new LostAndFoundBinRuntime(context);
        context.eventBus().subscribe(new LostAndFoundBinOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockInteractListener(runtime));
    }

}
