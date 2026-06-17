package dev.rono.igniscore.block.playerproximityalarm;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PlayerProximityAlarmOnBlockClickListener());
        context.eventBus().subscribe(new PlayerProximityAlarmOnBlockPlaceListener(context));
        context.eventBus().subscribe(new PlayerProximityAlarmOnBlockBreakListener());
    }

}
