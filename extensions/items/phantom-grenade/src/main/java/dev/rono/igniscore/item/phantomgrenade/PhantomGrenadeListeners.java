package dev.rono.igniscore.item.phantomgrenade;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class PhantomGrenadeListeners implements OnItemClickListener {
    private final PhantomGrenadeBehavior behavior;

    PhantomGrenadeListeners(IgnisStrategyContext context) {
        this.behavior = new PhantomGrenadeBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
