package dev.rono.igniscore.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Singleton
public class PlacedBlockPersistenceService implements AutoCloseable {
    private static final Type INDEX_TYPE = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

    private final IgnisRuntimeHost host;
    private final Path indexFile;
    private final Path legacyYamlFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, Map<String, String>> worldIndexes = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Map<String, String>>> chunkIndexes = new ConcurrentHashMap<>();
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "igniscore-placed-block-save");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicBoolean saveDirty = new AtomicBoolean();
    private volatile boolean closed;

    @Inject
    public PlacedBlockPersistenceService(IgnisRuntimeHost host) {
        this.host = host;
        this.indexFile = host.getDataDirectory().resolve("placed-blocks.json");
        this.legacyYamlFile = host.getDataDirectory().resolve("placed-blocks.yml");
        loadIndex();
    }

    public void recordPlacement(IgnisLocation location, String typeId) {
        IgnisLocation blockLocation = Locations.toBlock(location);
        String worldName = blockLocation.worldName();
        String key = blockKey(blockLocation);

        worldIndexes.computeIfAbsent(worldName, ignored -> new ConcurrentHashMap<>()).put(key, typeId);
        chunkIndexes
                .computeIfAbsent(worldName, ignored -> new ConcurrentHashMap<>())
                .computeIfAbsent(chunkKey(blockLocation), ignored -> new ConcurrentHashMap<>())
                .put(key, typeId);
        saveIndexAsync();
    }

    public void removePlacement(IgnisLocation location) {
        IgnisLocation blockLocation = Locations.toBlock(location);
        String worldName = blockLocation.worldName();
        String key = blockKey(blockLocation);
        Map<String, String> worldIndex = worldIndexes.get(worldName);
        if (worldIndex == null) {
            return;
        }

        if (worldIndex.remove(key) != null && worldIndex.isEmpty()) {
            worldIndexes.remove(worldName);
        }

        Map<String, Map<String, String>> worldChunks = chunkIndexes.get(worldName);
        if (worldChunks != null) {
            Map<String, String> chunkIndex = worldChunks.get(chunkKey(blockLocation));
            if (chunkIndex != null) {
                chunkIndex.remove(key);
                if (chunkIndex.isEmpty()) {
                    worldChunks.remove(chunkKey(blockLocation));
                }
            }
            if (worldChunks.isEmpty()) {
                chunkIndexes.remove(worldName);
            }
        }
        saveIndexAsync();
    }

    public Set<String> chunkKeysForWorld(String worldName) {
        Map<String, Map<String, String>> worldChunks = chunkIndexes.get(worldName);
        if (worldChunks == null || worldChunks.isEmpty()) {
            return Set.of();
        }
        return Set.copyOf(worldChunks.keySet());
    }

    public Map<String, String> entriesInChunk(String worldName, int chunkX, int chunkZ) {
        Map<String, Map<String, String>> worldChunks = chunkIndexes.get(worldName);
        if (worldChunks == null || worldChunks.isEmpty()) {
            return Map.of();
        }

        Map<String, String> chunkEntries = worldChunks.get(chunkCoordinateKey(chunkX, chunkZ));
        if (chunkEntries == null || chunkEntries.isEmpty()) {
            return Map.of();
        }
        return Map.copyOf(chunkEntries);
    }

    public void flush() {
        saveDirty.set(true);
        try {
            saveExecutor.submit(this::drainSaves).get();
        } catch (Exception error) {
            host.getLogger().log(Level.WARNING, "Failed to flush placed block index", error);
        }
    }

    public void shutdown() {
        if (closed) {
            return;
        }
        closed = true;
        flush();
        saveExecutor.shutdown();
        try {
            if (!saveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                saveExecutor.shutdownNow();
            }
        } catch (InterruptedException error) {
            saveExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        shutdown();
    }

    private void loadIndex() {
        worldIndexes.clear();
        chunkIndexes.clear();
        if (Files.isRegularFile(indexFile)) {
            loadJsonIndex();
            return;
        }
        if (Files.isRegularFile(legacyYamlFile)) {
            loadLegacyYamlIndex();
        }
    }

    private void loadJsonIndex() {
        try (Reader reader = Files.newBufferedReader(indexFile)) {
            Map<String, Map<String, String>> loaded = gson.fromJson(reader, INDEX_TYPE);
            if (loaded == null) {
                return;
            }
            mergeLoadedIndex(loaded);
        } catch (IOException error) {
            host.getLogger().log(Level.WARNING, "Failed to load placed block index", error);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadLegacyYamlIndex() {
        try (Reader reader = Files.newBufferedReader(legacyYamlFile)) {
            Object loaded = new Yaml().load(reader);
            if (!(loaded instanceof Map<?, ?> root)) {
                return;
            }
            Map<String, Map<String, String>> converted = new HashMap<>();
            for (Map.Entry<?, ?> worldEntry : root.entrySet()) {
                if (worldEntry.getKey() == null || !(worldEntry.getValue() instanceof Map<?, ?> worldIndex)) {
                    continue;
                }
                Map<String, String> entries = new HashMap<>();
                for (Map.Entry<?, ?> blockEntry : worldIndex.entrySet()) {
                    if (blockEntry.getKey() != null && blockEntry.getValue() != null) {
                        entries.put(String.valueOf(blockEntry.getKey()), String.valueOf(blockEntry.getValue()));
                    }
                }
                if (!entries.isEmpty()) {
                    converted.put(String.valueOf(worldEntry.getKey()), entries);
                }
            }
            mergeLoadedIndex(converted);
            saveIndex();
            try {
                Files.move(legacyYamlFile, legacyYamlFile.resolveSibling("placed-blocks.yml.migrated"));
            } catch (IOException error) {
                host.getLogger().log(Level.WARNING, "Migrated placed blocks to JSON but failed to rename legacy YAML file", error);
            }
            host.getLogger().info("Migrated placed block index from YAML to JSON");
        } catch (IOException error) {
            host.getLogger().log(Level.WARNING, "Failed to load legacy placed block YAML index", error);
        }
    }

    private void mergeLoadedIndex(Map<String, Map<String, String>> loaded) {
        for (Map.Entry<String, Map<String, String>> worldEntry : loaded.entrySet()) {
            if (worldEntry.getValue() == null || worldEntry.getValue().isEmpty()) {
                continue;
            }
            String worldName = worldEntry.getKey();
            Map<String, String> worldIndex = new ConcurrentHashMap<>(worldEntry.getValue());
            worldIndexes.put(worldName, worldIndex);
            rebuildChunkIndex(worldName, worldIndex);
        }
    }

    private void rebuildChunkIndex(String worldName, Map<String, String> worldIndex) {
        Map<String, Map<String, String>> worldChunks = new ConcurrentHashMap<>();
        for (Map.Entry<String, String> entry : worldIndex.entrySet()) {
            int[] coords = parseKey(entry.getKey());
            if (coords == null) {
                continue;
            }
            String chunkKey = chunkCoordinateKey(coords[0] >> 4, coords[2] >> 4);
            worldChunks.computeIfAbsent(chunkKey, ignored -> new ConcurrentHashMap<>())
                    .put(entry.getKey(), entry.getValue());
        }
        chunkIndexes.put(worldName, worldChunks);
    }

    private void saveIndexAsync() {
        if (closed) {
            return;
        }
        saveDirty.set(true);
        saveExecutor.execute(this::drainSaves);
    }

    private void drainSaves() {
        if (closed) {
            return;
        }
        while (saveDirty.getAndSet(false)) {
            if (closed) {
                return;
            }
            writeSnapshot(snapshotWorldIndexes());
        }
    }

    private Map<String, Map<String, String>> snapshotWorldIndexes() {
        Map<String, Map<String, String>> snapshot = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> worldEntry : worldIndexes.entrySet()) {
            snapshot.put(worldEntry.getKey(), Map.copyOf(worldEntry.getValue()));
        }
        return snapshot;
    }

    private void writeSnapshot(Map<String, Map<String, String>> snapshot) {
        try {
            Files.createDirectories(indexFile.getParent());
            try (Writer writer = Files.newBufferedWriter(indexFile)) {
                gson.toJson(snapshot, INDEX_TYPE, writer);
            }
        } catch (IOException error) {
            host.getLogger().log(Level.WARNING, "Failed to save placed block index", error);
            saveDirty.set(true);
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

    private static String chunkKey(IgnisLocation location) {
        return chunkCoordinateKey((int) Math.floor(location.x()) >> 4, (int) Math.floor(location.z()) >> 4);
    }

    private static String chunkCoordinateKey(int chunkX, int chunkZ) {
        return chunkX + "," + chunkZ;
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
