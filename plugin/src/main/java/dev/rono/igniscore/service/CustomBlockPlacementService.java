package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.platform.PlatformHooks;
import org.bukkit.plugin.Plugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomBlockPlacementService {
    private static final Material CUSTOM_BLOCK_BACKING_MATERIAL = Material.BARRIER;

    private final Plugin plugin;
    private final BlockManager blockManager;
    private final BlockItemIdentifier itemIdentifier;
    private final PlatformHooks platformHooks;

    @Inject
    public CustomBlockPlacementService(Main plugin,
                                       BlockManager blockManager,
                                       BlockItemIdentifier itemIdentifier,
                                       PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.itemIdentifier = itemIdentifier;
        this.platformHooks = platformHooks;
    }

    CustomBlockPlacementService(Plugin plugin,
                                BlockManager blockManager,
                                BlockItemIdentifier itemIdentifier,
                                PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.itemIdentifier = itemIdentifier;
        this.platformHooks = platformHooks;
    }

    public void handleInteractPlacement(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        String typeId = itemIdentifier.resolveTypeId(item);
        if (!isKnownType(typeId)) {
            return;
        }

        debug("Attempting to place custom block: " + typeId);
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Block targetBlock = clickedBlock.getRelative(event.getBlockFace());
        if (!targetBlock.getType().isAir() && !platformHooks.isBlockReplaceable(targetBlock)) {
            debug("Target block is not air or replaceable: " + targetBlock.getType());
            return;
        }

        event.setCancelled(true);
        targetBlock.setType(CUSTOM_BLOCK_BACKING_MATERIAL);
        blockManager.registerPlacedBlock(targetBlock.getLocation(), typeId);
        event.getPlayer().swingMainHand();
        debug("Successfully placed " + typeId + " at " + targetBlock.getLocation().toVector());

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && item != null) {
            item.setAmount(item.getAmount() - 1);
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        String typeId = itemIdentifier.resolveTypeId(event.getItemInHand());
        if (isKnownType(typeId)) {
            blockManager.registerPlacedBlock(event.getBlock().getLocation(), typeId);
        }
    }

    private boolean isKnownType(String typeId) {
        return typeId != null && blockManager.getBlockTypes().containsKey(typeId);
    }

    private void debug(String message) {
        if (plugin instanceof Main main) {
            main.debug(message);
        }
    }
}
