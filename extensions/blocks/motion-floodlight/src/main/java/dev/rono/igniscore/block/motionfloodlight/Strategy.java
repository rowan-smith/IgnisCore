package dev.rono.igniscore.block.motionfloodlight;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MotionFloodlightOnBlockClickListener());
        context.eventBus().subscribe(new MotionFloodlightOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MotionFloodlightOnBlockBreakListener());
    }

}
