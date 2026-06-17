package dev.rono.igniscore.block.echoblast;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        EchoBlastRuntime runtime = new EchoBlastRuntime(context);
        context.eventBus().subscribe(new EchoBlastOnBlockClickListener());
        context.eventBus().subscribe(new EchoBlastOnBlockTriggerListener(runtime));
    }

}
