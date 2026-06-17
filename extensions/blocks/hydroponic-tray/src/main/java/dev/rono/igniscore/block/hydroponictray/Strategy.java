package dev.rono.igniscore.block.hydroponictray;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new HydroponicTrayOnBlockClickListener());
        context.eventBus().subscribe(new HydroponicTrayOnBlockPlaceListener(context));
        context.eventBus().subscribe(new HydroponicTrayOnBlockBreakListener());
    }

}
