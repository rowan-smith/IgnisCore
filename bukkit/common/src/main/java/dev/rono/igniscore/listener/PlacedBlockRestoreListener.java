package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlacedBlockRestoreListener implements Listener {
    private final JavaPlugin plugin;
    private final BlockManager blockManager;
    private final PlacedBlockPersistenceService persistenceService;
    private final int blocksPerTick;
    private final ArrayDeque<PendingRestore> pendingRestores = new ArrayDeque<>();
    private final AtomicBoolean batchScheduled = new AtomicBoolean();

    @Inject
    public PlacedBlockRestoreListener(JavaPlugin plugin,
                                      BlockManager blockManager,
                                      PlacedBlockPersistenceService persistenceService,
                                      PerformanceSettings performanceSettings) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.persistenceService = persistenceService;
        this.blocksPerTick = performanceSettings.chunkRestoreBlocksPerTick();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        enqueueChunk(event.getChunk());
    }

    public void restoreLoadedChunks() {
        for (World world : Bukkit.getWorlds()) {
            for (String chunkKey : persistenceService.chunkKeysForWorld(world.getName())) {
                String[] parts = chunkKey.split(",");
                if (parts.length != 2) {
                    continue;
                }
                try {
                    int chunkX = Integer.parseInt(parts[0]);
                    int chunkZ = Integer.parseInt(parts[1]);
                    if (world.isChunkLoaded(chunkX, chunkZ)) {
                        enqueueChunk(world.getChunkAt(chunkX, chunkZ));
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        ensureBatchTaskRunning();
    }

    private void enqueueChunk(Chunk chunk) {
        Map<String, String> entries = persistenceService.entriesInChunk(
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (entries.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            pendingRestores.add(new PendingRestore(chunk, entry.getKey(), entry.getValue()));
        }
        ensureBatchTaskRunning();
    }

    private void ensureBatchTaskRunning() {
        if (pendingRestores.isEmpty() || !batchScheduled.compareAndSet(false, true)) {
            return;
        }
        plugin.getServer().scheduler().runTask(plugin, this::processBatchAndContinue);
    }

    private void processBatchAndContinue() {
        processBatch();
        if (pendingRestores.isEmpty()) {
            batchScheduled.set(false);
            return;
        }
        plugin.getServer().scheduler().runTaskLater(plugin, this::processBatchAndContinue, 1L);
    }

    private void processBatch() {
        int processed = 0;
        while (processed < blocksPerTick && !pendingRestores.isEmpty()) {
            PendingRestore restore = pendingRestores.poll();
            if (restore != null && tryRestore(restore)) {
                processed++;
            }
        }
    }

    private boolean tryRestore(PendingRestore restore) {
        Location location = parseLocation(restore.chunk(), restore.blockKey());
        if (location == null || blockManager.getPlacedBlockType(BukkitBridge.toIgnis(location)) != null) {
            return false;
        }

        Block block = location.getBlock();
        if (block.getType() != Material.BARRIER) {
            persistenceService.removePlacement(BukkitBridge.toIgnis(location));
            return false;
        }

        blockManager.restorePlacedBlock(BukkitBridge.toIgnis(location), restore.typeId());
        return true;
    }

    private Location parseLocation(Chunk chunk, String key) {
        String[] parts = key.split(",");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new Location(
                    chunk.getWorld(),
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private record PendingRestore(Chunk chunk, String blockKey, String typeId) {
    }
}
