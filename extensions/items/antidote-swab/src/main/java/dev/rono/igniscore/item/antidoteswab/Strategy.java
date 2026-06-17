package dev.rono.igniscore.item.antidoteswab;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        var listeners = new AntidoteSwabListeners(context);
        onItemClick(listeners);
    }

}

