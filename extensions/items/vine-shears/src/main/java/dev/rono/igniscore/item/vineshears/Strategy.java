package dev.rono.igniscore.item.vineshears;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final VineShearsBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new VineShearsBehavior(context);
        onItemClick(event -> {
            if ("use".equals(event.actionToken())) {
                behavior.onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
        });
    }

}

