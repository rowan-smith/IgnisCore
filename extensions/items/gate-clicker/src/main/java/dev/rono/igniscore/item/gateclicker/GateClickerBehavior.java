package dev.rono.igniscore.item.gateclicker;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.LinkItemSupport;

final class GateClickerBehavior {
    private final IgnisStrategyContext context;

    GateClickerBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        String blockType = StrategySupport.customString(definition.getCustomData(), "linkBlockType", "");
        String action = StrategySupport.customString(definition.getCustomData(), "remoteAction", "activate");
        double range = StrategySupport.customDouble(definition.getCustomData(), "linkRange", 64.0);
        LinkItemSupport.onUse(context, player, definition, item, clickedBlock, blockType, action, range);
    }
}
