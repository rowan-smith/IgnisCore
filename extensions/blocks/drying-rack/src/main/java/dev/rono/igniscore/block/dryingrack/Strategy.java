package dev.rono.igniscore.block.dryingrack;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new DryingRackOnBlockClickListener());
        context.eventBus().subscribe(new DryingRackOnBlockPlaceListener(context));
        context.eventBus().subscribe(new DryingRackOnBlockBreakListener());
    }

}
