package dev.rono.igniscore.block.ringtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        RingRuntime runtime = new RingRuntime(context);
        context.eventBus().subscribe(new RingOnBlockClickListener());
        context.eventBus().subscribe(new RingOnBlockTickListener(runtime));
        context.eventBus().subscribe(new RingOnBlockTriggerListener(runtime));
    }

}
