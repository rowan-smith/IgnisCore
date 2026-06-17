package dev.rono.igniscore.block.timedcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        TimedChargeRuntime runtime = new TimedChargeRuntime(context);
        context.eventBus().subscribe(new TimedChargeOnBlockClickListener());
        context.eventBus().subscribe(new TimedChargeOnBlockTickListener(runtime));
        context.eventBus().subscribe(new TimedChargeOnBlockTriggerListener(runtime));
    }

}
