package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new IceCreamFreezerOnBlockClickListener());
        IceCreamFreezerRuntime runtime = new IceCreamFreezerRuntime(context);
        context.eventBus().subscribe(new IceCreamFreezerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new IceCreamFreezerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new IceCreamFreezerOnBlockInteractListener(runtime));
    }

}
