package dev.rono.igniscore.block.breachingcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        BreachingChargeRuntime runtime = new BreachingChargeRuntime(context);
        context.eventBus().subscribe(new BreachingChargeOnBlockClickListener());
        context.eventBus().subscribe(new BreachingChargeOnBlockTriggerListener(runtime));
    }

}
