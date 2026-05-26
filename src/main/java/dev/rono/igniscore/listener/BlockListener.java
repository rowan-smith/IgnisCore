package dev.rono.igniscore.listener;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.manager.BlockManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockListener implements Listener {
    private final BlockManager manager;
    private final NamespacedKey blockTypeKey;

    public BlockListener(JavaPlugin plugin, BlockManager manager) {
        this.manager = manager;
        this.blockTypeKey = new NamespacedKey(plugin, "block_type");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        // Try to get block id from NBT
        String typeId = IgnisCoreAPI.getNbtService().readItem(item, nbt -> nbt.getString("ignis:block_id"));
        
        if (typeId == null || typeId.isEmpty()) {
            // Fallback to PersistentDataContainer for backward compatibility
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                typeId = meta.getPersistentDataContainer().get(blockTypeKey, PersistentDataType.STRING);
                if (typeId == null) {
                    typeId = meta.getPersistentDataContainer().get(new NamespacedKey("igniscore", "tnt_type"), PersistentDataType.STRING);
                }
            }
        }
        
        if (typeId != null && manager.getBlockTypes().containsKey(typeId)) {
            manager.registerPlacedBlock(event.getBlock().getLocation(), typeId);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        manager.unregisterPlacedBlock(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTNTPrime(org.bukkit.event.block.TNTPrimeEvent event) {
        String typeId = manager.getPlacedBlockType(event.getBlock().getLocation());
        if (typeId != null) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            manager.unregisterPlacedBlock(event.getBlock().getLocation());
            manager.triggerBlock(event.getBlock().getLocation(), typeId, event);
        }
    }
}
