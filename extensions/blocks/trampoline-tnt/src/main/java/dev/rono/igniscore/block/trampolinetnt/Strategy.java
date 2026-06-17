package dev.rono.igniscore.block.trampolinetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        TrampolineTntRuntime runtime = new TrampolineTntRuntime(context);
        context.eventBus().subscribe(new TrampolineTntOnBlockClickListener());
        context.eventBus().subscribe(new TrampolineTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new TrampolineTntOnBlockTriggerListener(runtime));
    }

}
