package dev.rono.igniscore.block.mobgrinderhub;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MobGrinderHubOnBlockClickListener());
        context.eventBus().subscribe(new MobGrinderHubOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MobGrinderHubOnBlockBreakListener());
    }

}
