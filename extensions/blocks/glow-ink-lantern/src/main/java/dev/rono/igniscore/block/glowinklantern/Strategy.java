package dev.rono.igniscore.block.glowinklantern;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new GlowInkLanternOnBlockClickListener());
        context.eventBus().subscribe(new GlowInkLanternOnBlockPlaceListener(context));
        context.eventBus().subscribe(new GlowInkLanternOnBlockBreakListener());
    }

}
