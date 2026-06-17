package dev.rono.igniscore.item.stickybomb;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class StickyBombListeners implements OnItemClickListener {
    private final StickyBombBehavior behavior;

    StickyBombListeners(IgnisStrategyContext context) {
        this.behavior = new StickyBombBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
