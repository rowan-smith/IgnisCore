package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class SpongeCustomBlockIgnitionService {
    private static final String ACTION_IGNITE = "ignite";

    private final BlockManager blockManager;
    private final SpongeCustomBlockBreakService breakService;
    private final SpongeConfiguredEffectService effectService;

    @Inject
    public SpongeCustomBlockIgnitionService(BlockManager blockManager,
                                            SpongeCustomBlockBreakService breakService,
                                            SpongeConfiguredEffectService effectService) {
        this.blockManager = blockManager;
        this.breakService = breakService;
        this.effectService = effectService;
    }

    public void ignite(BlockSnapshot block, BlockDefinition definition, ServerPlayer player, ItemStack ignitionItem) {
        if (player != null) {
            breakService.cancel(player.uniqueId());
        }

        ServerLocation location = block.location().orElse(null);
        if (location == null) {
            return;
        }

        var center = Locations.toCenter(SpongeBridge.toIgnis(location));
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(definition.getBehaviorConfig());
        Map<String, Object> igniteSettings = behavior.igniteEffects().asMap();
        if (igniteSettings.isEmpty()) {
            igniteSettings = getMap(definition.getInteractionSettings(), ACTION_IGNITE);
        }
        effectService.playSound(center, behavior.igniteSoundOr(
                getString(getMap(definition.getInteractionSettings(), ACTION_IGNITE), "sound", "ITEM_FLINTANDSTEEL_USE")),
                1.0f, 1.0f);
        effectService.spawnConfiguredParticles(center, getList(igniteSettings, "particles"), "minecraft:flame",
                18, 0.35, 0.35, 0.35, 0.03);

        blockManager.unregisterPlacedBlock(SpongeBridge.toIgnis(location));
        clearBlock(location);
        if (player != null && ignitionItem != null) {
            damageIgnitionItem(player, ignitionItem);
        }
        blockManager.triggerBlock(SpongeBridge.toIgnis(location), definition.getId(), player);
    }

    private void damageIgnitionItem(ServerPlayer player, ItemStack item) {
        if (player.gameMode().get().equals(GameModes.CREATIVE.get()) || item == null || item.isEmpty()) {
            return;
        }

        if (item.type().equals(ItemTypes.FIRE_CHARGE.get())) {
            int amount = item.quantity();
            if (amount <= 1) {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), ItemStack.empty());
            } else {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), item.withQuantity(amount - 1));
            }
            return;
        }

        int durability = item.get(Keys.ITEM_DURABILITY).orElse(0);
        int maxDurability = item.get(Keys.MAX_DURABILITY).orElse(1);
        int updated = durability + 1;
        ItemStack mutable = item.asMutable();
        if (updated >= maxDurability) {
            int amount = item.quantity();
            if (amount <= 1) {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), ItemStack.empty());
            } else {
                player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), item.withQuantity(amount - 1));
            }
            return;
        }
        mutable.offer(Keys.ITEM_DURABILITY, updated);
        player.inventory().equipment().set(EquipmentTypes.MAINHAND.get(), mutable);
    }

    private void clearBlock(ServerLocation location) {
        BlockSnapshot air = BlockSnapshot.builder()
                .from(location)
                .blockState(BlockTypes.AIR.get().defaultState())
                .build();
        location.restoreSnapshot(air, false, BlockChangeFlags.ALL);
    }
}
