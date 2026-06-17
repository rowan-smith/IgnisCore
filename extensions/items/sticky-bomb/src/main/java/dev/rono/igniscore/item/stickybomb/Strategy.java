package dev.rono.igniscore.item.stickybomb;

import dev.rono.igniscore.api.config.ItemBehaviorConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {
    private final StickyBombBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new StickyBombBehavior(context);
    }

    @Override
    public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        ItemBehaviorConfig config = ItemBehaviorConfig.from(definition.getBehaviorConfig());
        config.actionFor(action).ifPresent(token -> {
            if ("throw".equals(token)) {
                behavior.onItemUse(player, definition, item);
            }
        });
    }
}
