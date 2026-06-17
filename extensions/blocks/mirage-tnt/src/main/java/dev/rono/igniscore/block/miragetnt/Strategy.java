package dev.rono.igniscore.block.miragetnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        MirageTntRuntime runtime = new MirageTntRuntime(context);
        context.eventBus().subscribe(new MirageTntOnBlockClickListener());
        context.eventBus().subscribe(new MirageTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new MirageTntOnBlockTriggerListener(runtime));
    }

}
