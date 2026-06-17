package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        DepthChargeRuntime runtime = new DepthChargeRuntime(context);
        context.eventBus().subscribe(new DepthChargeOnBlockClickListener());
        context.eventBus().subscribe(new DepthChargeOnBlockTickListener(runtime));
        context.eventBus().subscribe(new DepthChargeOnBlockTriggerListener(runtime));
    }

}
