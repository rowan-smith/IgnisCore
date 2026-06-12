package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Singleton
public class PlacedBlockPersistenceService {
    private final Main plugin;
    private final File indexFile;
    private final Map<String, Map<String, String>> worldIndexes = new ConcurrentHashMap<>();

    @Inject
    public PlacedBlockPersistenceService(Main plugin) {
        this.plugin = plugin;
        this.indexFile = new File(plugin.getDataFolder(), "placed-blocks.yml");
        loadIndex();
    }

    public void recordPlacement(Location location, String typeId) {
        Location blockLocation = location.getBlock().getLocation();
        String worldName = requireWorldName(blockLocation);
        String key = blockKey(blockLocation);

        worldIndexes.computeIfAbsent(worldName, ignored -> new ConcurrentHashMap<>()).put(key, typeId);
        saveIndex();
    }

    public void removePlacement(Location location) {
        Location blockLocation = location.getBlock().getLocation();
        String worldName = requireWorldName(blockLocation);
        Map<String, String> worldIndex = worldIndexes.get(worldName);
        if (worldIndex == null) {
            return;
        }

        if (worldIndex.remove(blockKey(blockLocation)) != null && worldIndex.isEmpty()) {
            worldIndexes.remove(worldName);
        }
        saveIndex();
    }

    public Map<String, String> entriesInChunk(Chunk chunk) {
        Map<String, String> worldIndex = worldIndexes.get(chunk.getWorld().getName());
        if (worldIndex == null || worldIndex.isEmpty()) {
            return Map.of();
        }

        int minX = chunk.getX() << 4;
        int maxX = minX + 15;
        int minZ = chunk.getZ() << 4;
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
        if (!indexFile.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(indexFile);
        if (!config.isConfigurationSection("worlds")) {
            return;
        }

        for (String worldName : config.getConfigurationSection("worlds").getKeys(false)) {
            Map<String, String> worldIndex = new ConcurrentHashMap<>();
            for (String key : config.getConfigurationSection("worlds." + worldName).getKeys(false)) {
                String typeId = config.getString("worlds." + worldName + "." + key);
                if (typeId != null && !typeId.isEmpty()) {
                    worldIndex.put(key, typeId);
                }
            }
            if (!worldIndex.isEmpty()) {
                worldIndexes.put(worldName, worldIndex);
            }
        }
    }

    private void saveIndex() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Map<String, String>> worldEntry : worldIndexes.entrySet()) {
            for (Map.Entry<String, String> blockEntry : worldEntry.getValue().entrySet()) {
                config.set("worlds." + worldEntry.getKey() + "." + blockEntry.getKey(), blockEntry.getValue());
            }
        }

        try {
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                plugin.getLogger().warning("Could not create plugin data folder for placed block persistence.");
                return;
            }
            config.save(indexFile);
        } catch (IOException error) {
            plugin.getLogger().log(Level.WARNING, "Failed to save placed block index", error);
        }
    }

    private static String blockKey(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private static String requireWorldName(Location location) {
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Location world must not be null");
        }
        return location.getWorld().getName();
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
