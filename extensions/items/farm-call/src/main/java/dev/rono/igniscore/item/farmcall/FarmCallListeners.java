package dev.rono.igniscore.item.farmcall;

import dev.rono.extensions.shared.strategy.LinkItemSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class FarmCallListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    FarmCallListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                String blockType = StrategySupport.customString(event.definition().getCustomData(), "linkBlockType", "");
                String action = StrategySupport.customString(event.definition().getCustomData(), "remoteAction", "activate");
                double range = StrategySupport.customDouble(event.definition().getCustomData(), "linkRange", 64.0);
                LinkItemSupport.onUse(context, event.player(), event.definition(), event.item(), event.clickedBlock(), blockType, action, range);
            }
    }
}
