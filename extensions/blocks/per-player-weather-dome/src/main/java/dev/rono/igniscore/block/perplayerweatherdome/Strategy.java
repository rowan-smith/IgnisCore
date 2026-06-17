package dev.rono.igniscore.block.perplayerweatherdome;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PerPlayerWeatherDomeOnBlockClickListener());
        context.eventBus().subscribe(new PerPlayerWeatherDomeOnBlockPlaceListener(context));
        context.eventBus().subscribe(new PerPlayerWeatherDomeOnBlockBreakListener());
    }

}
