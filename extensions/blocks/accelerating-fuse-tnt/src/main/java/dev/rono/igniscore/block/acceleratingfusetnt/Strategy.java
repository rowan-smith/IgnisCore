package dev.rono.igniscore.block.acceleratingfusetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new AcceleratingFuseTntOnBlockClickListener());
        context.eventBus().subscribe(new AcceleratingFuseTntOnBlockTickListener(context));
        context.eventBus().subscribe(new AcceleratingFuseTntOnBlockTriggerListener(context));
    }

}
