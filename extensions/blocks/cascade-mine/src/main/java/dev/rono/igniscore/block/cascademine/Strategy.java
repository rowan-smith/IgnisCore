package dev.rono.igniscore.block.cascademine;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CascadeMineOnBlockClickListener());
        context.eventBus().subscribe(new CascadeMineOnBlockTickListener(context));
        context.eventBus().subscribe(new CascadeMineOnBlockTriggerListener(context));
    }

}
