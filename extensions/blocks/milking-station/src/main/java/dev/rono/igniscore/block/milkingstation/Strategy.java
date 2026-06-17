package dev.rono.igniscore.block.milkingstation;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MilkingStationOnBlockClickListener());
        context.eventBus().subscribe(new MilkingStationOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MilkingStationOnBlockBreakListener());
    }

}
