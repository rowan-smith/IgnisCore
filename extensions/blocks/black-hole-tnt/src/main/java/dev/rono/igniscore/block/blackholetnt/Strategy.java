package dev.rono.igniscore.block.blackholetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        BlackHoleTntRuntime runtime = new BlackHoleTntRuntime(context);
        context.eventBus().subscribe(new BlackHoleTntOnBlockClickListener());
        context.eventBus().subscribe(new BlackHoleTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new BlackHoleTntOnBlockTriggerListener(runtime));
    }

}
