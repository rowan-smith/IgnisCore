package dev.rono.igniscore.item.chunkgridoverlay;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final ChunkGridOverlayBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new ChunkGridOverlayBehavior(context);
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

