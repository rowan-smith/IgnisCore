package dev.rono.igniscore.block.fishsmokerrack;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new FishSmokerRackOnBlockClickListener());
        context.eventBus().subscribe(new FishSmokerRackOnBlockPlaceListener(context));
        context.eventBus().subscribe(new FishSmokerRackOnBlockBreakListener());
    }

}
