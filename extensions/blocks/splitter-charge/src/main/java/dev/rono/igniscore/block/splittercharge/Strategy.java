package dev.rono.igniscore.block.splittercharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SplitterChargeOnBlockClickListener());
        context.eventBus().subscribe(new SplitterChargeOnBlockTickListener(context));
        context.eventBus().subscribe(new SplitterChargeOnBlockTriggerListener(context));
    }

}
