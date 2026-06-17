package dev.rono.igniscore.item.grenade;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final GrenadeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new GrenadeBehavior(context);
    }

    @Override
    public void onItemAction(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                             IgnisInteraction action, IgnisBlock clickedBlock, String actionToken) {
        if ("throw".equals(actionToken)) {
            behavior.onItemUse(player, definition, item);
        }
    }
}
