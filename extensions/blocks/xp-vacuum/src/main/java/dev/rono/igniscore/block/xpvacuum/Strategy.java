package dev.rono.igniscore.block.xpvacuum;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new XpVacuumOnBlockClickListener());
        context.eventBus().subscribe(new XpVacuumOnBlockPlaceListener(context));
        context.eventBus().subscribe(new XpVacuumOnBlockBreakListener());
    }

}
