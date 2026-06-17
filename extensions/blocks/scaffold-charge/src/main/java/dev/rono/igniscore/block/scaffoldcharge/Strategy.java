package dev.rono.igniscore.block.scaffoldcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ScaffoldChargeOnBlockClickListener());
        context.eventBus().subscribe(new ScaffoldChargeOnBlockTickListener(context));
        context.eventBus().subscribe(new ScaffoldChargeOnBlockTriggerListener(context));
    }

}
