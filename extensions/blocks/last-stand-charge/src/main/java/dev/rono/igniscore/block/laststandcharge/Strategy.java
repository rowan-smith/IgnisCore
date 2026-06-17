package dev.rono.igniscore.block.laststandcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new LastStandChargeOnBlockClickListener());
        context.eventBus().subscribe(new LastStandChargeOnBlockTickListener(context));
        context.eventBus().subscribe(new LastStandChargeOnBlockTriggerListener(context));
    }

}
