package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.manager.PlacedBlockRegistry;
import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
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
    private final PlacedBlockRegistry blockRegistry;
    private final BlockItemIdentifier itemIdentifier;
    private final PlatformHooks platformHooks;
    private final IgnisPluginContext pluginContext;

    @Inject
    public CustomBlockPlacementService(IgnisPluginContext pluginContext,
                                       PlacedBlockRegistry blockRegistry,
                                       BlockItemIdentifier itemIdentifier,
                                       PlatformHooks platformHooks) {
        this.plugin = pluginContext.plugin();
        this.blockRegistry = blockRegistry;
        this.itemIdentifier = itemIdentifier;
        this.platformHooks = platformHooks;
        this.pluginContext = pluginContext;
    }

    CustomBlockPlacementService(Plugin plugin,
                                PlacedBlockRegistry blockRegistry,
                                BlockItemIdentifier itemIdentifier,
                                PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.blockRegistry = blockRegistry;
        this.itemIdentifier = itemIdentifier;
        this.platformHooks = platformHooks;
        this.pluginContext = null;
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
        blockRegistry.registerPlacedBlock(BukkitBridge.toIgnis(targetBlock.getLocation()), typeId, BukkitBridge.wrap(item));
        event.getPlayer().swingMainHand();
        debug("Successfully placed " + typeId + " at " + targetBlock.getLocation().toVector());

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && item != null) {
            item.setAmount(item.getAmount() - 1);
        }
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        String typeId = itemIdentifier.resolveTypeId(event.getItemInHand());
        if (isKnownType(typeId)) {
            blockRegistry.registerPlacedBlock(BukkitBridge.toIgnis(event.getBlock().getLocation()), typeId, BukkitBridge.wrap(event.getItemInHand()));
        }
    }

    private boolean isKnownType(String typeId) {
        return typeId != null && blockRegistry.getBlockTypes().containsKey(typeId);
    }

    private void debug(String message) {
        if (pluginContext != null) {
            pluginContext.debug(message);
        }
    }
}
