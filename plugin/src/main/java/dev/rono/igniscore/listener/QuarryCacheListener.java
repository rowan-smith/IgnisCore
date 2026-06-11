package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.quarrycache.QuarryCacheInventory;
import dev.rono.igniscore.service.quarrycache.QuarryCacheService;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuarryCacheListener implements Listener {
    private final BlockManager blockManager;
    private final QuarryCacheService quarryCacheService;

    @Inject
    public QuarryCacheListener(BlockManager blockManager, QuarryCacheService quarryCacheService) {
        this.blockManager = blockManager;
        this.quarryCacheService = quarryCacheService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (blockManager.getPlacedBlockType(block.getLocation()) != null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getGameMode() == GameMode.CREATIVE ? null : player.getInventory().getItemInMainHand();
        Collection<ItemStack> drops = new ArrayList<>(block.getDrops(tool, player));
        if (drops.isEmpty()) {
            return;
        }

        if (quarryCacheService.tryCollect(block.getLocation(), drops)) {
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        if (quarryCacheService.tryCollect(event.getLocation(), List.of(stack))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof QuarryCacheInventory cacheInventory)) {
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot < top.getSize() && QuarryCacheInventory.isSeparatorSlot(rawSlot)) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick() && event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            ItemStack moving = event.getCurrentItem();
            if (moving != null && !moving.getType().isAir() && !cacheInventory.accepts(moving)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof QuarryCacheInventory)) {
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < top.getSize() && QuarryCacheInventory.isSeparatorSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof QuarryCacheInventory cacheInventory) {
            cacheInventory.restoreSeparators();
        }
    }
}
