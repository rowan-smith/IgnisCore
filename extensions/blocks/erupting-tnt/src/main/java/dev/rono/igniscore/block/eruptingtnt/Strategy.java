package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new EruptingOnBlockClickListener());
        context.eventBus().subscribe(new EruptingOnBlockTickListener(context));
        context.eventBus().subscribe(new EruptingOnBlockTriggerListener(context));
    }

}
