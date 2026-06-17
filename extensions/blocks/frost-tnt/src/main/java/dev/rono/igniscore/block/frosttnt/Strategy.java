package dev.rono.igniscore.block.frosttnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        FrostTntRuntime runtime = new FrostTntRuntime(context);
        context.eventBus().subscribe(new FrostTntOnBlockClickListener());
        context.eventBus().subscribe(new FrostTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new FrostTntOnBlockTriggerListener(runtime));
    }

}
