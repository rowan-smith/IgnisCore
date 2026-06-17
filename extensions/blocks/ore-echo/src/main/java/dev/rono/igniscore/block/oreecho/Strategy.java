package dev.rono.igniscore.block.oreecho;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new OreEchoOnBlockClickListener());
        context.eventBus().subscribe(new OreEchoOnBlockPlaceListener(context));
        context.eventBus().subscribe(new OreEchoOnBlockBreakListener());
    }

}
