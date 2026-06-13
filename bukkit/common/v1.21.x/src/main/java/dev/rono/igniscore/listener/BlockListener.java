package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.service.BlockInteractionResolver;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.service.CustomBlockBreakService;
import dev.rono.igniscore.service.CustomBlockIgnitionService;
import dev.rono.igniscore.service.CustomBlockPlacementService;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.service.ItemIdentifier;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockListener implements Listener {
    private final BlockManager blockManager;
    private final BlockInteractionResolver interactionResolver;
    private final CustomBlockPlacementService placementService;
    private final CustomBlockBreakService breakService;
    private final CustomBlockIgnitionService ignitionService;
    private final ItemIdentifier itemIdentifier;
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public BlockListener(BlockManager blockManager,
                         BlockInteractionResolver interactionResolver,
                         CustomBlockPlacementService placementService,
                         CustomBlockBreakService breakService,
                         CustomBlockIgnitionService ignitionService,
                         ItemIdentifier itemIdentifier,
                         IgnisStrategyRegistry strategyRegistry) {
        this.blockManager = blockManager;
        this.interactionResolver = interactionResolver;
        this.placementService = placementService;
        this.breakService = breakService;
        this.ignitionService = ignitionService;
        this.itemIdentifier = itemIdentifier;
        this.strategyRegistry = strategyRegistry;
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

        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
        if (itemIdentifier.resolveTypeId(heldItem) != null) {
            event.setCancelled(true);
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
        if (blockManager.getPlacedBlockType(BukkitBridge.toIgnis(event.getBlock().getLocation())) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTNTPrime(org.bukkit.event.block.TNTPrimeEvent event) {
        String typeId = blockManager.getPlacedBlockType(BukkitBridge.toIgnis(event.getBlock().getLocation()));
        if (typeId == null) {
            return;
        }

        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        blockManager.unregisterPlacedBlock(BukkitBridge.toIgnis(event.getBlock().getLocation()));
        blockManager.triggerBlock(BukkitBridge.toIgnis(event.getBlock().getLocation()), typeId, event);

        Entity primingEntity = event.getPrimingEntity();
        if (primingEntity instanceof Projectile) {
            primingEntity.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;
        if (event.getEntity().getFireTicks() <= 0) return;

        BlockDefinition definition = getPlacedDefinition(event.getHitBlock());
        if (definition == null) return;

        ignitionService.ignite(event.getHitBlock(), definition, null, null);
        event.getEntity().remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getBlockPower() <= 0) return;

        BlockDefinition definition = getPlacedDefinition(block);
        if (definition == null) return;

        ignitionService.ignite(block, definition, null, null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        BlockDefinition definition = getPlacedDefinition(event.getBlock());
        if (definition == null) return;

        ignitionService.ignite(event.getBlock(), definition, null, null);

        Entity igniter = event.getIgnitingEntity();
        if (igniter instanceof Projectile) {
            igniter.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    private void handleExplosion(List<Block> blockList) {
        for (Block block : blockList) {
            BlockDefinition definition = getPlacedDefinition(block);
            if (definition != null) {
                ignitionService.ignite(block, definition, null, null);
            }
        }
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
        String clickSide = switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> "LEFT_CLICK";
            case RIGHT_CLICK_BLOCK -> "RIGHT_CLICK";
            default -> "";
        };
        String materialKey = heldItem != null && !heldItem.getType().isAir() ? heldItem.getType().name() : "AIR";
        CustomBlockAction action = interactionResolver.resolve(definition, clickSide, materialKey);
        if (action == CustomBlockAction.NONE) {
            return false;
        }

        event.setCancelled(true);
        if (action == CustomBlockAction.IGNITE) {
            ignitionService.ignite(clickedBlock, definition, event.getPlayer(), heldItem);
        } else if (action == CustomBlockAction.BREAK) {
            breakService.start(event.getPlayer(), clickedBlock, definition);
        } else {
            requireBlockStrategy(definition).onStaticInteract(
                    definition,
                    BukkitBridge.toIgnis(clickedBlock.getLocation()),
                    BukkitBridge.wrap(event.getPlayer()),
                    action);
        }
        return true;
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        return strategyRegistry.requireBlockStrategy(definition.getExtensionId(), definition.getId());
    }

    private BlockDefinition getPlacedDefinition(Block block) {
        String typeId = blockManager.getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation()));
        if (typeId == null) {
            return null;
        }
        return blockManager.getBlockTypes().get(typeId);
    }
}
