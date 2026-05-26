package dev.rono.igniscore.listener;

import dev.rono.igniscore.manager.TNTManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class TNTListener implements Listener {
    private final TNTManager manager;
    private final NamespacedKey tntTypeKey;

    public TNTListener(JavaPlugin plugin, TNTManager manager) {
        this.manager = manager;
        this.tntTypeKey = new NamespacedKey(plugin, "tnt_type");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.TNT) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String typeId = meta.getPersistentDataContainer().get(tntTypeKey, PersistentDataType.STRING);
        if (typeId != null && manager.getTntTypes().containsKey(typeId)) {
            // Cancel vanilla block placement and spawn our entity instead
            event.setCancelled(true);
            
            // Remove 1 item from hand
            if (event.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) {
                item.setAmount(item.getAmount() - 1);
            }
            
            manager.spawnTNT(event.getBlock().getLocation(), typeId);
        }
    }
}
