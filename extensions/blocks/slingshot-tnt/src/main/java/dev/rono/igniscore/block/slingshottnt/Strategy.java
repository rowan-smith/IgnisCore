package dev.rono.igniscore.block.slingshottnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SlingshotTntRuntime runtime = new SlingshotTntRuntime(context);
        context.eventBus().subscribe(new SlingshotTntOnBlockClickListener());
        context.eventBus().subscribe(new SlingshotTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new SlingshotTntOnBlockTriggerListener(runtime));
    }

}
