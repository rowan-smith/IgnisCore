package dev.rono.igniscore.item.smokegrenade;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SmokeGrenadeListeners implements OnItemClickListener {
    private final SmokeGrenadeBehavior behavior;

    SmokeGrenadeListeners(IgnisStrategyContext context) {
        this.behavior = new SmokeGrenadeBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
