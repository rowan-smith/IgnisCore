package dev.rono.igniscore.item.decoyflare;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class DecoyFlareListeners implements OnItemClickListener {
    private final DecoyFlareBehavior behavior;

    DecoyFlareListeners(IgnisStrategyContext context) {
        this.behavior = new DecoyFlareBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
