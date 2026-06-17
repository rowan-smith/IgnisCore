package dev.rono.igniscore.block.infuser;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new InfuserOnBlockClickListener());
        context.eventBus().subscribe(new InfuserOnBlockPlaceListener(context));
        context.eventBus().subscribe(new InfuserOnBlockBreakListener());
    }

}
