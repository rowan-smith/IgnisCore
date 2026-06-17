package dev.rono.igniscore.block.itempedestalhologram;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ItemPedestalHologramOnBlockClickListener());
        context.eventBus().subscribe(new ItemPedestalHologramOnBlockPlaceListener(context));
        context.eventBus().subscribe(new ItemPedestalHologramOnBlockBreakListener());
    }

}
