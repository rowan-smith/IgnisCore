package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final DetonatorBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new DetonatorBehavior(new DetonatorLinkStorage(context.nbt()));
        onItemClick(event -> {
            switch (event.actionToken()) {
                case "assign" -> behavior.assignBomb(event.player(), event.definition(), event.item(), event.clickedBlock());
                case "detonate" -> behavior.detonateLinkedBombs(event.player(), event.definition(), event.item());
                default -> { }
            }
        });
    }

}

