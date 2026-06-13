package dev.rono.igniscore.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Singleton
public class PlacedBlockPersistenceService {
    private static final Type INDEX_TYPE = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

    private final IgnisRuntimeHost host;
    private final Path indexFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, Map<String, String>> worldIndexes = new ConcurrentHashMap<>();

    @Inject
    public PlacedBlockPersistenceService(IgnisRuntimeHost host) {
        this.host = host;
        this.indexFile = host.getDataDirectory().resolve("placed-blocks.json");
        loadIndex();
    }

    public void recordPlacement(IgnisLocation location, String typeId) {
        IgnisLocation blockLocation = Locations.toBlock(location);
        String worldName = blockLocation.worldName();
        String key = blockKey(blockLocation);

        worldIndexes.computeIfAbsent(worldName, ignored -> new ConcurrentHashMap<>()).put(key, typeId);
        saveIndex();
    }

    public void removePlacement(IgnisLocation location) {
        IgnisLocation blockLocation = Locations.toBlock(location);
        String worldName = blockLocation.worldName();
        Map<String, String> worldIndex = worldIndexes.get(worldName);
        if (worldIndex == null) {
            return;
        }

        if (worldIndex.remove(blockKey(blockLocation)) != null && worldIndex.isEmpty()) {
            worldIndexes.remove(worldName);
        }
        saveIndex();
    }

    public Map<String, String> entriesInChunk(String worldName, int chunkX, int chunkZ) {
        Map<String, String> worldIndex = worldIndexes.get(worldName);
        if (worldIndex == null || worldIndex.isEmpty()) {
            return Map.of();
        }

        int minX = chunkX << 4;
        int maxX = minX + 15;
        int minZ = chunkZ << 4;
        int maxZ = minZ + 15;

        Map<String, String> entries = new ConcurrentHashMap<>();
        for (Map.Entry<String, String> entry : worldIndex.entrySet()) {
            int[] coords = parseKey(entry.getKey());
            if (coords == null) {
                continue;
            }
            if (coords[0] >= minX && coords[0] <= maxX && coords[2] >= minZ && coords[2] <= maxZ) {
                entries.put(entry.getKey(), entry.getValue());
            }
        }
        return entries;
    }

    private void loadIndex() {
        worldIndexes.clear();
        if (!Files.isRegularFile(indexFile)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(indexFile)) {
            Map<String, Map<String, String>> loaded = gson.fromJson(reader, INDEX_TYPE);
            if (loaded == null) {
                return;
            }
            for (Map.Entry<String, Map<String, String>> worldEntry : loaded.entrySet()) {
                if (worldEntry.getValue() != null && !worldEntry.getValue().isEmpty()) {
                    worldIndexes.put(worldEntry.getKey(), new ConcurrentHashMap<>(worldEntry.getValue()));
                }
            }
        } catch (IOException error) {
            host.getLogger().log(Level.WARNING, "Failed to load placed block index", error);
        }
    }

    private void saveIndex() {
        try {
            Files.createDirectories(indexFile.getParent());
            try (Writer writer = Files.newBufferedWriter(indexFile)) {
                gson.toJson(new HashMap<>(worldIndexes), INDEX_TYPE, writer);
            }
        } catch (IOException error) {
            host.getLogger().log(Level.WARNING, "Failed to save placed block index", error);
        }
    }

    private static String blockKey(IgnisLocation location) {
        return (int) Math.floor(location.x()) + "," + (int) Math.floor(location.y()) + "," + (int) Math.floor(location.z());
    }

    private static int[] parseKey(String key) {
        String[] parts = key.split(",");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new int[] {
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            };
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
