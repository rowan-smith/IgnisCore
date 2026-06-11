package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.model.ItemDefinition;
import dev.rono.igniscore.service.ItemIdentifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {
    private final ItemManager itemManager;
    private final ItemIdentifier itemIdentifier;
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public ItemListener(ItemManager itemManager,
                        ItemIdentifier itemIdentifier,
                        IgnisStrategyRegistry strategyRegistry) {
        this.itemManager = itemManager;
        this.itemIdentifier = itemIdentifier;
        this.strategyRegistry = strategyRegistry;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getHand() == EquipmentSlot.OFF_HAND
                ? event.getPlayer().getInventory().getItemInOffHand()
                : event.getPlayer().getInventory().getItemInMainHand();

        String typeId = itemIdentifier.resolveTypeId(item);
        if (typeId == null) {
            return;
        }

        ItemDefinition definition = itemManager.getItemTypes().get(typeId);
        if (definition == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        strategyRegistry.get(definition.getStrategy()).onItemUse(player, definition, item, event.getAction());

        if (item.getAmount() <= 0) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }
}
