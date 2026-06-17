package dev.rono.igniscore.item.rivetgun;

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final RivetGunBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new RivetGunBehavior(context);
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

