package dev.rono.igniscore.block.mudslidetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        MudslideTntRuntime runtime = new MudslideTntRuntime(context);
        context.eventBus().subscribe(new MudslideTntOnBlockClickListener());
        context.eventBus().subscribe(new MudslideTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new MudslideTntOnBlockTriggerListener(runtime));
    }

}
