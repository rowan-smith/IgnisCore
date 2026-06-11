package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static dev.rono.igniscore.util.ConfigValueReader.asInt;
import static dev.rono.igniscore.util.ConfigValueReader.getInt;
import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class CustomBlockBreakService {
    private static final int CUSTOM_BLOCK_BREAK_TICKS = 35;

    private final Main plugin;
    private final BlockManager blockManager;
    private final ConfiguredEffectService effectService;
    private final IgnisStrategyRegistry strategyRegistry;
    private final Map<UUID, MiningSession> miningSessions = new ConcurrentHashMap<>();

    @Inject
    public CustomBlockBreakService(Main plugin,
                                   BlockManager blockManager,
                                   ConfiguredEffectService effectService,
                                   IgnisStrategyRegistry strategyRegistry) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.effectService = effectService;
        this.strategyRegistry = strategyRegistry;
    }

    public void start(Player player, Block block, BlockDefinition definition) {
        String typeId = definition.getId();
        if (player.getGameMode() == GameMode.CREATIVE) {
            breakBlock(block, definition, false);
            return;
        }

        if (!definition.isBreakable()) {
            return;
        }

        MiningSession existing = miningSessions.get(player.getUniqueId());
        if (existing != null && existing.location.equals(block.getLocation())) {
            return;
        }

        cancel(player.getUniqueId());

        Location location = block.getLocation();
        int totalTicks = getBreakTicks(definition, player.getInventory().getItemInMainHand());
        MiningSession session = new MiningSession(location, typeId, player.getEntityId(), totalTicks);
        session.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> tickMiningSession(player, block, definition, session),
                0L, 1L);

        miningSessions.put(player.getUniqueId(), session);
    }

    public void cancelIfMatching(Player player, Block block) {
        MiningSession session = miningSessions.get(player.getUniqueId());
        if (session != null && session.location.equals(block.getLocation())) {
            cancel(player.getUniqueId());
        }
    }

    public void cancel(UUID playerId) {
        MiningSession session = miningSessions.remove(playerId);
        if (session == null) {
            return;
        }
        if (session.task != null) {
            session.task.cancel();
        }
        sendBlockDamage(session.location, 0.0f, session.sourceEntityId);
    }

    public void breakBlock(Block block, BlockDefinition definition, boolean dropItem) {
        if (!definition.isBreakable()) {
            return;
        }

        Location center = Locations.toCenter(block.getLocation());
        effectService.playSound(center, getString(definition.getBreakSettings(), "break_sound", "BLOCK_STONE_BREAK"),
                0.8f, 1.0f);
        effectService.spawnConfiguredParticles(center, getList(getMap(definition.getBreakSettings(), "particles"), "break"),
                Particle.BLOCK, 24, 0.35, 0.35, 0.35, 0.01);

        requireBlockStrategy(definition).onStaticBreak(definition, block.getLocation());

        if (dropItem) {
            block.getWorld().dropItemNaturally(block.getLocation(), plugin.createBlockItem(definition.getId()));
        }
        blockManager.unregisterPlacedBlock(block.getLocation());
        block.setType(Material.AIR);
    }

    private void tickMiningSession(Player player, Block block, BlockDefinition definition, MiningSession session) {
        if (!player.isOnline() || !session.typeId.equals(blockManager.getPlacedBlockType(session.location))) {
            cancel(player.getUniqueId());
            return;
        }

        session.elapsedTicks++;
        float progress = Math.min(1.0f, session.elapsedTicks / (float) session.totalTicks);
        sendBlockDamage(session.location, progress, session.sourceEntityId);

        int hitInterval = getInt(definition.getBreakSettings(), "hit_interval_ticks", 6);
        if (hitInterval > 0 && session.elapsedTicks % hitInterval == 0) {
            effectService.playSound(Locations.toCenter(session.location),
                    getString(definition.getBreakSettings(), "hit_sound", "BLOCK_STONE_HIT"), 0.25f, 1.2f);
            effectService.spawnConfiguredParticles(Locations.toCenter(session.location),
                    getList(getMap(definition.getBreakSettings(), "particles"), "hit"),
                    Particle.CRIT, 3, 0.3, 0.3, 0.3, 0.02);
        }

        if (session.elapsedTicks >= session.totalTicks) {
            cancel(player.getUniqueId());
            breakBlock(block, definition, true);
        }
    }

    private int getBreakTicks(BlockDefinition definition, ItemStack tool) {
        int baseTicks = getInt(definition.getBreakSettings(), "ticks", CUSTOM_BLOCK_BREAK_TICKS);
        if (tool == null) {
            return baseTicks;
        }

        String name = tool.getType().name();
        Map<String, Object> toolModifiers = getMap(definition.getBreakSettings(), "tool_ticks");
        for (Map.Entry<String, Object> entry : toolModifiers.entrySet()) {
            String suffix = entry.getKey().toUpperCase();
            if (name.endsWith(suffix)) {
                return Math.max(1, asInt(entry.getValue(), baseTicks));
            }
        }

        return baseTicks;
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        var strategy = strategyRegistry.get(definition.getStrategy());
        if (!(strategy instanceof IgnisBlockStrategy blockStrategy)) {
            throw new IllegalStateException("Block type " + definition.getId() + " uses a non-block strategy: "
                    + definition.getStrategy());
        }
        return blockStrategy;
    }

    private void sendBlockDamage(Location location, float progress, int sourceEntityId) {
        for (Player viewer : location.getWorld().getPlayers()) {
            viewer.sendBlockDamage(location, progress, sourceEntityId);
        }
    }

    private static class MiningSession {
        private final Location location;
        private final String typeId;
        private final int sourceEntityId;
        private final int totalTicks;
        private int elapsedTicks;
        private BukkitTask task;

        private MiningSession(Location location, String typeId, int sourceEntityId, int totalTicks) {
            this.location = location;
            this.typeId = typeId;
            this.sourceEntityId = sourceEntityId;
            this.totalTicks = totalTicks;
        }
    }
}
