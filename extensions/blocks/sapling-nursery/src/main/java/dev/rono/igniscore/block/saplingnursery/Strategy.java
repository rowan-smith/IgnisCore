package dev.rono.igniscore.block.saplingnursery;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SaplingNurseryOnBlockClickListener());
        SaplingNurseryRuntime runtime = new SaplingNurseryRuntime(context);
        context.eventBus().subscribe(new SaplingNurseryOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockInteractListener(runtime));
    }

}
