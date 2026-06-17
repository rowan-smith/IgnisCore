package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        TripwireChargeRuntime runtime = new TripwireChargeRuntime(context);
        context.eventBus().subscribe(new TripwireChargeOnBlockClickListener());
        context.eventBus().subscribe(new TripwireChargeOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new TripwireChargeOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new TripwireChargeOnBlockTriggerListener(runtime));
    }

}
