package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.ExtensionSupportService;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtensionSupportListener implements Listener {
    private final BlockManager blockManager;
    private final ExtensionSupportService extensionSupport;

    @Inject
    public ExtensionSupportListener(BlockManager blockManager, ExtensionSupportService extensionSupport) {
        this.blockManager = blockManager;
        this.extensionSupport = extensionSupport;
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

        if (extensionSupport.tryCollect(block.getLocation(), drops)) {
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        if (extensionSupport.tryCollect(event.getLocation(), List.of(stack))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(top);
        if (customInventory == null) {
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot < top.getSize() && customInventory.isSeparatorSlot(rawSlot)) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick() && event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            ItemStack moving = event.getCurrentItem();
            if (moving != null && !moving.getType().isAir() && !customInventory.accepts(moving)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(top);
        if (customInventory == null) {
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < top.getSize() && customInventory.isSeparatorSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(event.getInventory());
        if (customInventory != null) {
            customInventory.restoreDecorations();
        }
    }
}
