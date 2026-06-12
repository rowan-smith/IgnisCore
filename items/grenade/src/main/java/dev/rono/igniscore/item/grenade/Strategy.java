package dev.rono.igniscore.item.grenade;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Strategy extends AbstractIgnisItemStrategy {
    private final GrenadeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new GrenadeBehavior(context.getPlugin());
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action) {
        if (context == null) {
            return;
        }
        behavior.onItemUse(player, definition, item);
    }
}
