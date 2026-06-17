package dev.rono.igniscore.item.farmcall;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final FarmCallBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new FarmCallBehavior(context);
    }

    @Override
    public void registerEvents() {
        onItemClick(event -> {
            if ("use".equals(event.actionToken())) {
                behavior.onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
        });
    }
}

