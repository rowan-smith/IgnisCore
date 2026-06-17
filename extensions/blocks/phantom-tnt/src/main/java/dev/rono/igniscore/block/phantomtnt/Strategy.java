package dev.rono.igniscore.block.phantomtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PhantomOnBlockClickListener());
        context.eventBus().subscribe(new PhantomOnBlockTickListener(context));
        context.eventBus().subscribe(new PhantomOnBlockTriggerListener(context));
    }

}
