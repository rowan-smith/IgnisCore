package dev.rono.igniscore.item.fragmentationgrenade;

import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class FragmentationGrenadeListeners implements OnItemClickListener {
    private final FragmentationGrenadeBehavior behavior;

    FragmentationGrenadeListeners(IgnisStrategyContext context) {
        this.behavior = new FragmentationGrenadeBehavior(context);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("throw".equals(event.actionToken())) {
            behavior.onItemUse(event.player(), event.definition(), event.item());
        }
    }
}
