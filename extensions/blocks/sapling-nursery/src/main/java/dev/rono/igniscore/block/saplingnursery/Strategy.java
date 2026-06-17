package dev.rono.igniscore.block.saplingnursery;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        SaplingNurseryRuntime runtime = new SaplingNurseryRuntime(context);
        context.eventBus().subscribe(new SaplingNurseryOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockInteractListener(runtime));
    }

}
