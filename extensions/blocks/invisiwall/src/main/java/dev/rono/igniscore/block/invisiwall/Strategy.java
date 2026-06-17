package dev.rono.igniscore.block.invisiwall;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        InvisiwallRuntime runtime = new InvisiwallRuntime(context);
        context.eventBus().subscribe(new InvisiwallOnBlockClickListener());
        context.eventBus().subscribe(new InvisiwallOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new InvisiwallOnBlockTriggerListener(runtime));
    }

}
