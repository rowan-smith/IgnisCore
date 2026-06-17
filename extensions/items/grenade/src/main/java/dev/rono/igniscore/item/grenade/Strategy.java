package dev.rono.igniscore.item.grenade;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final GrenadeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new GrenadeBehavior(context);
    }

    @Override
    public void registerEvents() {
        onItemClick(event -> {
            if ("throw".equals(event.actionToken())) {
                behavior.onItemUse(event.player(), event.definition(), event.item());
            }
        });
    }
}
