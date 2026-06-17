package dev.rono.igniscore.block.mobradar;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MobRadarOnBlockClickListener());
        context.eventBus().subscribe(new MobRadarOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MobRadarOnBlockBreakListener());
    }

}
