package dev.rono.igniscore.item.sprinklertimer;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        var listeners = new SprinklerTimerListeners(context);
        onItemClick(listeners);
    }

}

