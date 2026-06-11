package dev.rono.items.detonator;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.ExplosiveStrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.model.ItemDefinition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Strategy extends AbstractIgnisItemStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action) {
        double range = ExplosiveStrategySupport.customDouble(definition.getCustomData(), "range", 8.0);
        Location center = player.getLocation();
        int triggered = 0;

        for (int x = (int) -range; x <= range; x++) {
            for (int y = (int) -range; y <= range; y++) {
                for (int z = (int) -range; z <= range; z++) {
                    if (x * x + y * y + z * z > range * range) {
                        continue;
                    }

                    Location blockLocation = center.clone().add(x, y, z).getBlock().getLocation();
                    String typeId = IgnisCoreAPI.getPlacedBlockType(blockLocation);
                    if (typeId == null) {
                        continue;
                    }

                    IgnisCoreAPI.triggerBlock(blockLocation, typeId, player);
                    triggered++;
                }
            }
        }

        if (triggered > 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.4f);
            item.setAmount(item.getAmount() - 1);
        }
    }
}
