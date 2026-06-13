package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final DetonatorBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new DetonatorBehavior(new DetonatorLinkStorage(context.getNbtService()));
    }

    @Override
    public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        switch (definition.interactionAction(action)) {
            case "assign_bomb" -> behavior.assignBomb(player, definition, item, clickedBlock);
            case "detonate_linked" -> behavior.detonateLinkedBombs(player, definition, item);
            default -> {
            }
        }
    }
}
