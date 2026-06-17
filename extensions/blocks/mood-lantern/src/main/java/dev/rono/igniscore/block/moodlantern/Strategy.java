package dev.rono.igniscore.block.moodlantern;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MoodLanternOnBlockClickListener());
        context.eventBus().subscribe(new MoodLanternOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MoodLanternOnBlockBreakListener());
    }

}
