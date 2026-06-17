package dev.rono.igniscore.item.flashbang;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FlashbangListeners implements OnItemClickListener {
    private final FlashbangBehavior behavior;

    FlashbangListeners(IgnisStrategyContext context) {
        this.behavior = new FlashbangBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
