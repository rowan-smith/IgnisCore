package dev.rono.igniscore.block.featherfallcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        FeatherfallChargeRuntime runtime = new FeatherfallChargeRuntime(context);
        context.eventBus().subscribe(new FeatherfallChargeOnBlockClickListener());
        context.eventBus().subscribe(new FeatherfallChargeOnBlockTriggerListener(runtime));
    }

}
