package dev.rono.igniscore.block.xpvacuum;

import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new XpVacuumOnBlockClickListener());
        XpVacuumListeners listeners = new XpVacuumListeners(context);
        context.eventBus().subscribe((OnBlockPlaceListener) listeners);
        context.eventBus().subscribe((OnBlockBreakListener) listeners);
    }

}
