package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.service.ItemIdentifier;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
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
    private final StrategyEventPublisher events;

    @Inject
    public ItemListener(ItemManager itemManager,
                        ItemIdentifier itemIdentifier,
                        StrategyEventPublisher events) {
        this.itemManager = itemManager;
        this.itemIdentifier = itemIdentifier;
        this.events = events;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_BLOCK) {
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
        events.fireItemClick(
                BukkitBridge.wrap(player),
                definition,
                BukkitBridge.wrap(item),
                BukkitBridge.toIgnisInteraction(action),
                event.getClickedBlock() != null ? BukkitBridge.wrap(event.getClickedBlock()) : null);

        if (item.getAmount() <= 0) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }
}
