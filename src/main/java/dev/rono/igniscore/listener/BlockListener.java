package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.service.BlockInteractionResolver;
import dev.rono.igniscore.service.CustomBlockAction;
import dev.rono.igniscore.service.CustomBlockBreakService;
import dev.rono.igniscore.service.CustomBlockIgnitionService;
import dev.rono.igniscore.service.CustomBlockPlacementService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
    private final BlockManager blockManager;
    private final BlockInteractionResolver interactionResolver;
    private final CustomBlockPlacementService placementService;
    private final CustomBlockBreakService breakService;
    private final CustomBlockIgnitionService ignitionService;

    @Inject
    public BlockListener(BlockManager blockManager,
                         BlockInteractionResolver interactionResolver,
                         CustomBlockPlacementService placementService,
                         CustomBlockBreakService breakService,
                         CustomBlockIgnitionService ignitionService) {
        this.blockManager = blockManager;
        this.interactionResolver = interactionResolver;
        this.placementService = placementService;
        this.breakService = breakService;
        this.ignitionService = ignitionService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (handleCustomBlockInteraction(event)) {
            return;
        }
        placementService.handleInteractPlacement(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        placementService.handleBlockPlace(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        BlockDefinition definition = getPlacedDefinition(event.getBlock());
        if (definition == null) {
            return;
        }

        event.setCancelled(true);
        event.setDropItems(false);
        breakService.start(event.getPlayer(), event.getBlock(), definition);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        BlockDefinition definition = getPlacedDefinition(event.getBlock());
        if (definition == null) {
            return;
        }

        event.setCancelled(true);
        event.setInstaBreak(false);
        breakService.start(event.getPlayer(), event.getBlock(), definition);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageAbort(BlockDamageAbortEvent event) {
        breakService.cancelIfMatching(event.getPlayer(), event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onNotePlay(org.bukkit.event.block.NotePlayEvent event) {
        if (blockManager.getPlacedBlockType(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTNTPrime(org.bukkit.event.block.TNTPrimeEvent event) {
        String typeId = blockManager.getPlacedBlockType(event.getBlock().getLocation());
        if (typeId == null) {
            return;
        }

        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        blockManager.unregisterPlacedBlock(event.getBlock().getLocation());
        blockManager.triggerBlock(event.getBlock().getLocation(), typeId, event);
    }

    private boolean handleCustomBlockInteraction(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return false;
        }

        BlockDefinition definition = getPlacedDefinition(clickedBlock);
        if (definition == null) {
            return false;
        }

        ItemStack heldItem = event.getItem();
        CustomBlockAction action = interactionResolver.resolve(definition, event.getAction(), heldItem);
        if (action == CustomBlockAction.NONE) {
            return false;
        }

        event.setCancelled(true);
        if (action == CustomBlockAction.IGNITE) {
            ignitionService.ignite(clickedBlock, definition, event.getPlayer(), heldItem);
        } else if (action == CustomBlockAction.BREAK) {
            breakService.start(event.getPlayer(), clickedBlock, definition);
        }
        return true;
    }

    private BlockDefinition getPlacedDefinition(Block block) {
        String typeId = blockManager.getPlacedBlockType(block.getLocation());
        if (typeId == null) {
            return null;
        }
        return blockManager.getBlockTypes().get(typeId);
    }
}
