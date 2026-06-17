package dev.rono.igniscore.block.signalcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SignalChargeOnBlockClickListener());
        context.eventBus().subscribe(new SignalChargeOnBlockTriggerListener(context));
    }

}
