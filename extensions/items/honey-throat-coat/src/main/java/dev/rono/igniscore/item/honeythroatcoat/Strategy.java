package dev.rono.igniscore.item.honeythroatcoat;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final HoneyThroatCoatBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new HoneyThroatCoatBehavior(context);
        onItemClick(event -> {
            if ("use".equals(event.actionToken())) {
                behavior.onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
        });
    }

}

