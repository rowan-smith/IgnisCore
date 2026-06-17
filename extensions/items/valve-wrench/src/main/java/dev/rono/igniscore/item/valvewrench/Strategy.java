package dev.rono.igniscore.item.valvewrench;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        var listeners = new ValveWrenchListeners(context);
        onItemClick(listeners);
    }

}

