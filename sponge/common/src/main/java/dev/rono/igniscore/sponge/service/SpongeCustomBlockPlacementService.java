package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
import dev.rono.igniscore.manager.PlacedBlockRegistry;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.api.port.PlatformAdapter;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3i;

public class SpongeCustomBlockPlacementService {
    private final PlacedBlockRegistry blockRegistry;
    private final SpongeBlockItemIdentifier itemIdentifier;
    private final PlatformAdapter platformAdapter;
    private final SpongePluginContext pluginContext;

    @Inject
    public SpongeCustomBlockPlacementService(SpongePluginContext pluginContext,
                                             PlacedBlockRegistry blockRegistry,
                                             SpongeBlockItemIdentifier itemIdentifier,
                                             PlatformAdapter platformAdapter) {
        this.pluginContext = pluginContext;
        this.blockRegistry = blockRegistry;
        this.itemIdentifier = itemIdentifier;
        this.platformAdapter = platformAdapter;
    }

    public void handleInteractPlacement(InteractBlockEvent.Secondary event, ServerPlayer player) {
        ItemStack item = player.inventory().equipment()
                .peek(EquipmentTypes.MAINHAND.get())
                .map(ItemStack::asMutable)
                .orElse(null);
        String typeId = itemIdentifier.resolveTypeId(item);
        if (!isKnownType(typeId)) {
            return;
        }

        pluginContext.debug("Attempting to place custom block: " + typeId);
        BlockSnapshot clickedBlock = event.block();
        if (clickedBlock == null || clickedBlock.state().type().isAnyOf(BlockTypes.AIR.get())) {
            return;
        }

        Direction face = event.targetSide();
        Vector3i targetPosition = clickedBlock.position().add(face.asBlockOffset());
        ServerLocation targetLocation = clickedBlock.location()
                .map(location -> location.world().location(targetPosition.x(), targetPosition.y(), targetPosition.z()))
                .orElse(null);
        if (targetLocation == null) {
            return;
        }

        BlockSnapshot targetBlock = targetLocation.createSnapshot();
        if (!targetBlock.state().type().isAnyOf(BlockTypes.AIR.get()) && !platformAdapter.isBlockReplaceable(targetBlock)) {
            pluginContext.debug("Target block is not air or replaceable: " + SpongeBridge.materialKey(targetBlock.state().type()));
            return;
        }

        event.setCancelled(true);
        placeBackingBlock(targetLocation);
        var ignisLocation = SpongeBridge.toIgnis(targetLocation);
        PlacedMetaSupport.recordPlacementYaw(ignisLocation, (float) player.rotation().y());
        blockRegistry.registerPlacedBlock(ignisLocation, typeId, SpongeBridge.wrap(item));
        pluginContext.debug("Successfully placed " + typeId + " at " + targetPosition);

        if (!player.gameMode().get().equals(GameModes.CREATIVE.get()) && item != null) {
            int amount = item.quantity();
            if (amount <= 1) {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), ItemStack.empty());
            } else {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), withQuantity(item, amount - 1));
            }
        }
    }

    public void handleBlockPlace(ChangeBlockEvent.All event, ServerPlayer player, BlockSnapshot placedBlock, ItemStack item) {
        String typeId = itemIdentifier.resolveTypeId(item);
        if (!isKnownType(typeId)) {
            return;
        }
        placedBlock.location().ifPresent(location -> {
            var ignisLocation = SpongeBridge.toIgnis(location);
            PlacedMetaSupport.recordPlacementYaw(ignisLocation, (float) player.rotation().y());
            blockRegistry.registerPlacedBlock(ignisLocation, typeId, SpongeBridge.wrap(item));
        });
    }

    private void placeBackingBlock(ServerLocation location) {
        BlockSnapshot barrier = BlockSnapshot.builder()
                .from(location)
                .blockState(BlockTypes.BARRIER.get().defaultState())
                .build();
        location.restoreSnapshot(barrier, false, BlockChangeFlags.ALL);
    }

    private boolean isKnownType(String typeId) {
        return typeId != null && blockRegistry.getBlockTypes().containsKey(typeId);
    }

    private static ItemStack withQuantity(ItemStack item, int quantity) {
        if (quantity <= 0) {
            return ItemStack.empty();
        }
        return ItemStack.builder().from(item).quantity(quantity).build();
    }
}
