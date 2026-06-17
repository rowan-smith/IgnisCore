package dev.rono.igniscore.block.infernotnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        InfernoTntRuntime runtime = new InfernoTntRuntime(context);
        context.eventBus().subscribe(new InfernoTntOnBlockClickListener());
        context.eventBus().subscribe(new InfernoTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new InfernoTntOnBlockTriggerListener(runtime));
    }

}
