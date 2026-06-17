package dev.rono.igniscore.item.farmerstea;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final FarmersTeaBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new FarmersTeaBehavior(context);
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

