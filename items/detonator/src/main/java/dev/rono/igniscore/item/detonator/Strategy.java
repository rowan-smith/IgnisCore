package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Strategy extends AbstractIgnisItemStrategy {
    private final DetonatorBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new DetonatorBehavior(new DetonatorLinkStorage(context.getNbtService()));
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action, Block clickedBlock) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            behavior.assignBomb(player, definition, item, clickedBlock);
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            behavior.detonateLinkedBombs(player, definition, item);
        }
    }
}
