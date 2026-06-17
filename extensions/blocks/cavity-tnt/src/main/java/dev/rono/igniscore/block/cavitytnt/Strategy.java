package dev.rono.igniscore.block.cavitytnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        CavityRuntime runtime = new CavityRuntime(context);
        context.eventBus().subscribe(new CavityOnBlockClickListener());
        context.eventBus().subscribe(new CavityOnBlockTickListener(runtime));
        context.eventBus().subscribe(new CavityOnBlockTriggerListener(runtime));
    }

}
