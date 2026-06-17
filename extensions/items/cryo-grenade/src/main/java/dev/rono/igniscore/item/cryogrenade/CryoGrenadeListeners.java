package dev.rono.igniscore.item.cryogrenade;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class CryoGrenadeListeners implements OnItemClickListener {
    private final CryoGrenadeBehavior behavior;

    CryoGrenadeListeners(IgnisStrategyContext context) {
        this.behavior = new CryoGrenadeBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
