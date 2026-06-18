package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static dev.rono.igniscore.util.ConfigValueReader.asInt;
import static dev.rono.igniscore.util.ConfigValueReader.getInt;
import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class SpongeCustomBlockBreakService {
    private static final int CUSTOM_BLOCK_BREAK_TICKS = 35;

    private final PlatformAdapter platformAdapter;
    private final BlockManager blockManager;
    private final SpongeBlockItemFactory blockItemFactory;
    private final SpongeConfiguredEffectService effectService;
    private final StrategyEventPublisher events;
    private final Map<UUID, MiningSession> miningSessions = new ConcurrentHashMap<>();

    @Inject
    public SpongeCustomBlockBreakService(PlatformAdapter platformAdapter,
                                         BlockManager blockManager,
                                         SpongeBlockItemFactory blockItemFactory,
                                         SpongeConfiguredEffectService effectService,
                                         StrategyEventPublisher events) {
        this.platformAdapter = platformAdapter;
        this.blockManager = blockManager;
        this.blockItemFactory = blockItemFactory;
        this.effectService = effectService;
        this.events = events;
    }

    public void start(ServerPlayer player, BlockSnapshot block, BlockDefinition definition) {
        if (player.gameMode().get().equals(GameModes.CREATIVE.get())) {
            breakBlock(block, definition, false);
            return;
        }

        if (!definition.isBreakable()) {
            return;
        }

        ServerLocation location = block.location().orElse(null);
        if (location == null) {
            return;
        }

        MiningSession existing = miningSessions.get(player.uniqueId());
        if (existing != null && existing.location.equals(location)) {
            return;
        }

        cancel(player.uniqueId());

        ItemStack tool = player.inventory().equipment().peek(EquipmentTypes.MAINHAND.get()).orElse(null);
        int totalTicks = getBreakTicks(definition, tool);
        if (totalTicks <= 0) {
            sendBlockDamage(location, 1.0f, player);
            breakBlock(block, definition, true);
            return;
        }

        MiningSession session = new MiningSession(location, definition.getId(), player, totalTicks);
        session.task = platformAdapter.getScheduler().runRepeating(
                SpongeBridge.toIgnis(location),
                () -> tickMiningSession(player, block, definition, session),
                0L,
                1L);
        miningSessions.put(player.uniqueId(), session);
    }

    public void cancelIfMatching(ServerPlayer player, BlockSnapshot block) {
        MiningSession session = miningSessions.get(player.uniqueId());
        block.location().ifPresent(location -> {
            if (session != null && session.location.equals(location)) {
                cancel(player.uniqueId());
            }
        });
    }

    public void cancel(UUID playerId) {
        MiningSession session = miningSessions.remove(playerId);
        if (session == null) {
            return;
        }
        if (session.task != null) {
            session.task.cancel();
        }
        sendBlockDamage(session.location, 0.0f, session.player);
    }

    public void breakBlock(BlockSnapshot block, BlockDefinition definition, boolean dropItem) {
        if (!definition.isBreakable()) {
            return;
        }

        ServerLocation location = block.location().orElse(null);
        if (location == null) {
            return;
        }

        IgnisLocation center = Locations.toCenter(SpongeBridge.toIgnis(location));
        effectService.playSound(center, getString(definition.getBreakSettings(), "break_sound", "BLOCK_STONE_BREAK"),
                0.8f, 1.0f);
        effectService.spawnConfiguredParticles(center, getList(getMap(definition.getBreakSettings(), "particles"), "break"),
                "minecraft:block", 24, 0.35, 0.35, 0.35, 0.01);

        ItemStack droppedItem = dropItem ? blockItemFactory.createBlockItem(definition.getId()) : null;
        events.fireBlockBreak(
                definition,
                SpongeBridge.toIgnis(location),
                droppedItem != null ? SpongeBridge.wrap(droppedItem) : null);

        if (droppedItem != null) {
            dropItemNaturally(location, droppedItem);
        }
        blockManager.unregisterPlacedBlock(SpongeBridge.toIgnis(location));
        clearBlock(location);
    }

    private void tickMiningSession(ServerPlayer player, BlockSnapshot block, BlockDefinition definition, MiningSession session) {
        if (!player.isOnline()
                || !session.typeId.equals(blockManager.getPlacedBlockType(SpongeBridge.toIgnis(session.location)))) {
            cancel(player.uniqueId());
            return;
        }

        session.elapsedTicks++;
        float progress = Math.min(1.0f, session.elapsedTicks / (float) session.totalTicks);
        sendBlockDamage(session.location, progress, session.player);

        int hitInterval = getInt(definition.getBreakSettings(), "hit_interval_ticks", 6);
        if (hitInterval > 0 && session.elapsedTicks % hitInterval == 0) {
            IgnisLocation center = Locations.toCenter(SpongeBridge.toIgnis(session.location));
            effectService.playSound(center, getString(definition.getBreakSettings(), "hit_sound", "BLOCK_STONE_HIT"),
                    0.25f, 1.2f);
            effectService.spawnConfiguredParticles(center, getList(getMap(definition.getBreakSettings(), "particles"), "hit"),
                    "minecraft:crit", 3, 0.3, 0.3, 0.3, 0.02);
        }

        if (session.elapsedTicks >= session.totalTicks) {
            cancel(player.uniqueId());
            breakBlock(block, definition, true);
        }
    }

    private int getBreakTicks(BlockDefinition definition, ItemStack tool) {
        Map<String, Object> breakSettings = definition.getBreakSettings();
        int baseTicks;
        if (breakSettings.containsKey("ticks")) {
            baseTicks = getInt(breakSettings, "ticks", CUSTOM_BLOCK_BREAK_TICKS);
        } else if (isInstantBreakBlock(definition)) {
            baseTicks = 0;
        } else {
            baseTicks = CUSTOM_BLOCK_BREAK_TICKS;
        }

        if (tool == null || tool.isEmpty()) {
            return baseTicks;
        }

        String name = SpongeBridge.materialKey(tool.type()).toUpperCase();
        Map<String, Object> toolModifiers = getMap(definition.getBreakSettings(), "tool_ticks");
        for (Map.Entry<String, Object> entry : toolModifiers.entrySet()) {
            String suffix = entry.getKey().toUpperCase();
            if (name.endsWith(suffix)) {
                return Math.max(0, asInt(entry.getValue(), baseTicks));
            }
        }

        return baseTicks;
    }

    private boolean isInstantBreakBlock(BlockDefinition definition) {
        return BlockBehaviorConfig.from(definition.getBehaviorConfig()).combustible();
    }

    private void sendBlockDamage(ServerLocation location, float progress, ServerPlayer sourcePlayer) {
        for (ServerPlayer viewer : location.world().players()) {
            try {
                viewer.sendBlockProgress(
                        location.blockPosition(),
                        Math.max(0.0, Math.min(1.0, progress)));
            } catch (RuntimeException ignored) {
            }
        }
    }

    private void clearBlock(ServerLocation location) {
        BlockSnapshot air = BlockSnapshot.builder()
                .from(location)
                .blockState(BlockTypes.AIR.get().defaultState())
                .build();
        location.restoreSnapshot(air, false, BlockChangeFlags.ALL);
    }

    private void dropItemNaturally(ServerLocation location, ItemStack itemStack) {
        var entity = location.createEntity(EntityTypes.ITEM.get());
        if (entity instanceof Item itemEntity) {
            itemEntity.item().set(itemStack.createSnapshot());
            location.spawnEntity(itemEntity);
        }
    }

    private static final class MiningSession {
        private final ServerLocation location;
        private final String typeId;
        private final ServerPlayer player;
        private final int totalTicks;
        private int elapsedTicks;
        private IgnisTask task;

        private MiningSession(ServerLocation location, String typeId, ServerPlayer player, int totalTicks) {
            this.location = location;
            this.typeId = typeId;
            this.player = player;
            this.totalTicks = totalTicks;
        }
    }
}
