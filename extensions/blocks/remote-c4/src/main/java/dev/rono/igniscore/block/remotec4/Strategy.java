package dev.rono.igniscore.block.remotec4;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        RemoteC4Runtime runtime = new RemoteC4Runtime(context);
        context.eventBus().subscribe(new RemoteC4OnBlockClickListener());
        context.eventBus().subscribe(new RemoteC4OnBlockTriggerListener(runtime));
    }

}
