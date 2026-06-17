package dev.rono.igniscore.block.pausetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PauseTntOnBlockClickListener());
        context.eventBus().subscribe(new PauseTntOnBlockTickListener(context));
        context.eventBus().subscribe(new PauseTntOnBlockTriggerListener(context));
    }

}
