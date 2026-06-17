package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        BouncingBettyRuntime runtime = new BouncingBettyRuntime(context);
        context.eventBus().subscribe(new BouncingBettyOnBlockClickListener());
        context.eventBus().subscribe(new BouncingBettyOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new BouncingBettyOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new BouncingBettyOnBlockTriggerListener(runtime));
    }

}
