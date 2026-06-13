package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Map;

public class PlacedBlockRestoreListener implements Listener {
    private final BlockManager blockManager;
    private final PlacedBlockPersistenceService persistenceService;

    @Inject
    public PlacedBlockRestoreListener(BlockManager blockManager, PlacedBlockPersistenceService persistenceService) {
        this.blockManager = blockManager;
        this.persistenceService = persistenceService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        restoreChunk(event.getChunk());
    }

    public void restoreLoadedChunks() {
        for (World world : org.bukkit.Bukkit.getWorlds()) {
            for (String chunkKey : persistenceService.chunkKeysForWorld(world.getName())) {
                String[] parts = chunkKey.split(",");
                if (parts.length != 2) {
                    continue;
                }
                try {
                    int chunkX = Integer.parseInt(parts[0]);
                    int chunkZ = Integer.parseInt(parts[1]);
                    if (world.isChunkLoaded(chunkX, chunkZ)) {
                        restoreChunk(world.getChunkAt(chunkX, chunkZ));
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private void restoreChunk(Chunk chunk) {
        Map<String, String> entries = persistenceService.entriesInChunk(
                chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (entries.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Location location = parseLocation(chunk, entry.getKey());
            if (location == null || blockManager.getPlacedBlockType(BukkitBridge.toIgnis(location)) != null) {
                continue;
            }

            Block block = location.getBlock();
            if (block.getType() != Material.BARRIER) {
                persistenceService.removePlacement(BukkitBridge.toIgnis(location));
                continue;
            }

            blockManager.restorePlacedBlock(BukkitBridge.toIgnis(location), entry.getValue());
        }
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

}
