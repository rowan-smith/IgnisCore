package dev.rono.igniscore.block.oresniffer;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new OreSnifferOnBlockClickListener());
        context.eventBus().subscribe(new OreSnifferOnBlockPlaceListener(context));
        context.eventBus().subscribe(new OreSnifferOnBlockBreakListener());
    }

}
