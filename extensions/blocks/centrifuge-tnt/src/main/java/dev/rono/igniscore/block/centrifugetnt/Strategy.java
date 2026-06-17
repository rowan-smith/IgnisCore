package dev.rono.igniscore.block.centrifugetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        CentrifugeTntRuntime runtime = new CentrifugeTntRuntime(context);
        context.eventBus().subscribe(new CentrifugeTntOnBlockClickListener());
        context.eventBus().subscribe(new CentrifugeTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new CentrifugeTntOnBlockTriggerListener(runtime));
    }

}
