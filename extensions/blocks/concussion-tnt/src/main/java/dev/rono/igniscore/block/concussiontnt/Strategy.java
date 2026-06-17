package dev.rono.igniscore.block.concussiontnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ConcussionRuntime runtime = new ConcussionRuntime(context);
        context.eventBus().subscribe(new ConcussionOnBlockClickListener());
        context.eventBus().subscribe(new ConcussionOnBlockTickListener(runtime));
        context.eventBus().subscribe(new ConcussionOnBlockTriggerListener(runtime));
    }

}
