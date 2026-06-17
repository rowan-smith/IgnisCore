package dev.rono.igniscore.block.lightbeacon;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new LightBeaconOnBlockClickListener());
        context.eventBus().subscribe(new LightBeaconOnBlockPlaceListener(context));
        context.eventBus().subscribe(new LightBeaconOnBlockBreakListener());
    }

}
