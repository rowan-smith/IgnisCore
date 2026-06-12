package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
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
        for (Chunk chunk : eventWorldLoadedChunks()) {
            restoreChunk(chunk);
        }
    }

    private void restoreChunk(Chunk chunk) {
        Map<String, String> entries = persistenceService.entriesInChunk(chunk);
        if (entries.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Location location = parseLocation(chunk, entry.getKey());
            if (location == null || blockManager.getPlacedBlockType(location) != null) {
                continue;
            }

            Block block = location.getBlock();
            if (block.getType() != Material.BARRIER) {
                persistenceService.removePlacement(location);
                continue;
            }

            blockManager.restorePlacedBlock(location, entry.getValue());
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

    private Chunk[] eventWorldLoadedChunks() {
        return org.bukkit.Bukkit.getWorlds().stream()
                .flatMap(world -> {
                    Chunk[] chunks = world.getLoadedChunks();
                    return java.util.Arrays.stream(chunks);
                })
                .toArray(Chunk[]::new);
    }
}
