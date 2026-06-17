package dev.rono.igniscore.item.lampdimmer;

import dev.rono.extensions.shared.strategy.LinkItemSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class LampDimmerListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    LampDimmerListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        String blockType = StrategySupport.customString(definition.getCustomData(), "linkBlockType", "");
        String action = StrategySupport.customString(definition.getCustomData(), "remoteAction", "activate");
        double range = StrategySupport.customDouble(definition.getCustomData(), "linkRange", 64.0);
        LinkItemSupport.onUse(context, player, definition, item, clickedBlock, blockType, action, range);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
    }
}
