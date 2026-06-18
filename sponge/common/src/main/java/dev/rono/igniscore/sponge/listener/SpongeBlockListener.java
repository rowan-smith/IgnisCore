package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockBreakService;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockIgnitionService;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockPlacementService;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.entity.explosive.PrimeExplosiveEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.sound.PlaySoundEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.world.server.ServerLocation;

public class SpongeBlockListener {
    private final BlockManager blockManager;
    private final SpongeCustomBlockPlacementService placementService;
    private final SpongeCustomBlockBreakService breakService;
    private final SpongeCustomBlockIgnitionService ignitionService;
    private final SpongeItemIdentifier itemIdentifier;
    private final StrategyEventPublisher events;

    @Inject
    public SpongeBlockListener(BlockManager blockManager,
                               SpongeCustomBlockPlacementService placementService,
                               SpongeCustomBlockBreakService breakService,
                               SpongeCustomBlockIgnitionService ignitionService,
                               SpongeItemIdentifier itemIdentifier,
                               StrategyEventPublisher events) {
        this.blockManager = blockManager;
        this.placementService = placementService;
        this.breakService = breakService;
        this.ignitionService = ignitionService;
        this.itemIdentifier = itemIdentifier;
        this.events = events;
    }

    @Listener(order = Order.LATE)
    public void onSecondaryInteract(InteractBlockEvent.Secondary event, @First ServerPlayer player) {
        if (handleCustomBlockInteraction(event, player, IgnisInteraction.RIGHT_CLICK_BLOCK)) {
            return;
        }
        placementService.handleInteractPlacement(event, player);
    }

    @Listener(order = Order.LATE)
    public void onPrimaryInteract(InteractBlockEvent.Primary event, @First ServerPlayer player) {
        handleCustomBlockInteraction(event, player, IgnisInteraction.LEFT_CLICK_BLOCK);
    }

    @Listener(order = Order.LATE)
    public void onPrimaryInteractStart(InteractBlockEvent.Primary.Start event, @First ServerPlayer player) {
        BlockDefinition definition = getPlacedDefinition(event.block());
        if (definition == null) {
            return;
        }

        ItemStack heldItem = player.inventory().equipment().peek(EquipmentTypes.MAINHAND.get()).orElse(null);
        if (itemIdentifier.resolveTypeId(heldItem) != null) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        breakService.start(player, event.block(), definition);
    }

    @Listener(order = Order.LATE)
    public void onPrimaryInteractStop(InteractBlockEvent.Primary.Stop event, @First ServerPlayer player) {
        breakService.cancelIfMatching(player, event.block());
    }

    @Listener(order = Order.LATE)
    public void onBlockChange(ChangeBlockEvent.All event, @First ServerPlayer player) {
        event.transactions(Operations.PLACE.get()).forEach(transaction -> {
            ItemStack item = player.inventory().equipment().peek(EquipmentTypes.MAINHAND.get()).orElse(null);
            placementService.handleBlockPlace(event, player, transaction.finalReplacement(), item);
        });

        event.transactions(Operations.BREAK.get()).forEach(transaction -> {
            BlockDefinition definition = getPlacedDefinition(transaction.original());
            if (definition == null) {
                return;
            }
            transaction.invalidate();
            breakService.start(player, transaction.original(), definition);
        });
    }

    @Listener(order = Order.LATE)
    public void onNotePlay(PlaySoundEvent.NoteBlock event) {
        ServerLocation location = event.location();
        if (blockManager.getPlacedBlockType(SpongeBridge.toIgnis(location)) != null) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.LATE)
    public void onTntPrime(PrimeExplosiveEvent.Pre event) {
        ServerLocation location = event.fusedExplosive().serverLocation();
        var blockLocation = SpongeBridge.toIgnis(location);
        String typeId = blockManager.getPlacedBlockType(blockLocation);
        if (typeId == null) {
            return;
        }

        event.setCancelled(true);
        blockManager.unregisterPlacedBlock(blockLocation);
        location.restoreSnapshot(org.spongepowered.api.block.BlockSnapshot.builder()
                .from(location)
                .blockState(org.spongepowered.api.block.BlockTypes.AIR.get().defaultState())
                .build(), false, org.spongepowered.api.world.BlockChangeFlags.ALL);
        blockManager.triggerBlock(blockLocation, typeId, event);
        event.cause().first(Projectile.class).ifPresent(Entity::remove);
    }

    @Listener(order = Order.LATE)
    public void onProjectileImpact(CollideBlockEvent.Impact event) {
        Entity entity = event.cause().first(Entity.class).orElse(null);
        if (entity == null || entity.get(Keys.FIRE_TICKS).orElse(0) <= 0) {
            return;
        }

        BlockDefinition definition = getPlacedDefinition(event.targetLocation().createSnapshot());
        if (definition == null) {
            return;
        }

        ignitionService.ignite(event.targetLocation().createSnapshot(), definition, null, null);
        entity.remove();
    }

    @Listener(order = Order.LATE)
    public void onNeighborNotify(NotifyNeighborBlockEvent event) {
        for (var ticket : event.tickets()) {
            BlockSnapshot target = ticket.target();
            BlockDefinition definition = getPlacedDefinition(target);
            if (definition == null) {
                continue;
            }
            boolean powered = target.state().get(Keys.IS_POWERED).orElse(false)
                    || target.state().get(Keys.IS_INDIRECTLY_POWERED).orElse(false)
                    || target.state().get(Keys.POWER).orElse(0) > 0;
            if (powered) {
                ignitionService.ignite(target, definition, null, null);
            }
        }
    }

    @Listener(order = Order.LATE)
    public void onEntityExplode(ExplosionEvent.Detonate event) {
        handleExplosion(event.affectedLocations());
    }

    private void handleExplosion(java.util.List<ServerLocation> locations) {
        for (ServerLocation location : locations) {
            BlockDefinition definition = getPlacedDefinition(location.createSnapshot());
            if (definition != null) {
                ignitionService.ignite(location.createSnapshot(), definition, null, null);
            }
        }
    }

    private boolean handleCustomBlockInteraction(InteractBlockEvent event,
                                                 ServerPlayer player,
                                                 IgnisInteraction interaction) {
        BlockSnapshot clickedBlock = event.block();
        if (clickedBlock == null) {
            return false;
        }

        BlockDefinition definition = getPlacedDefinition(clickedBlock);
        if (definition == null) {
            return false;
        }

        ItemStack heldItem = player.inventory().equipment().peek(EquipmentTypes.MAINHAND.get()).orElse(null);
        IgnisItem ignisHeldItem = heldItem != null && !heldItem.isEmpty()
                ? SpongeBridge.wrap(heldItem)
                : null;
        CustomBlockAction action = events.fireBlockClick(
                definition,
                SpongeBridge.toIgnis(clickedBlock.location().orElseThrow()),
                SpongeBridge.wrap(player),
                interaction,
                ignisHeldItem);
        if (action == CustomBlockAction.NONE) {
            return false;
        }

        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
        if (action == CustomBlockAction.IGNITE) {
            ignitionService.ignite(clickedBlock, definition, player, heldItem);
        } else if (action == CustomBlockAction.BREAK) {
            breakService.start(player, clickedBlock, definition);
        } else if (action == CustomBlockAction.OPEN) {
            events.fireBlockInteract(
                    definition,
                    SpongeBridge.toIgnis(clickedBlock.location().orElseThrow()),
                    SpongeBridge.wrap(player),
                    interaction,
                    ignisHeldItem,
                    action);
        }
        return true;
    }

    private BlockDefinition getPlacedDefinition(BlockSnapshot block) {
        String typeId = block.location()
                .map(SpongeBridge::toIgnis)
                .map(blockManager::getPlacedBlockType)
                .orElse(null);
        if (typeId == null) {
            return null;
        }
        return blockManager.getBlockTypes().get(typeId);
    }
}
