package dev.rono.igniscore.item.atlasimprinter;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final AtlasImprinterBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new AtlasImprinterBehavior(context);
        onItemClick(event -> {
            if ("use".equals(event.actionToken())) {
                behavior.onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
        });
    }

}

