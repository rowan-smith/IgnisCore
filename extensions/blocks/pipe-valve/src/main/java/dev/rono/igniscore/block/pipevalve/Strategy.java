package dev.rono.igniscore.block.pipevalve;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PipeValveOnBlockClickListener());
        context.eventBus().subscribe(new PipeValveOnBlockPlaceListener(context));
        context.eventBus().subscribe(new PipeValveOnBlockBreakListener());
    }

}
