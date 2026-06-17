package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SeismicRuntime runtime = new SeismicRuntime(context);
        context.eventBus().subscribe(new SeismicOnBlockClickListener());
        context.eventBus().subscribe(new SeismicOnBlockTickListener(runtime));
        context.eventBus().subscribe(new SeismicOnBlockTriggerListener(runtime));
    }

}
