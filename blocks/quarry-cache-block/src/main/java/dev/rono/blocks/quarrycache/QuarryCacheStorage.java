package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

final class QuarryCacheStorage {
    static final String NBT_KEY = "ignis:quarry_cache_contents";

    private final Plugin plugin;
    private final IgnisNbtService nbtService;
    private final File baseDir;

    QuarryCacheStorage(Plugin plugin, IgnisNbtService nbtService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
        this.baseDir = plugin == null ? null : new File(plugin.getDataFolder(), "quarry-cache");
    }

    void save(Location location, QuarryCacheInventory inventory) {
        if (baseDir == null) {
            return;
        }
        File file = fileFor(location);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().warning("Could not create quarry cache storage directory: " + parent);
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("world", requireWorldName(location));
        config.set("x", location.getBlockX());
        config.set("y", location.getBlockY());
        config.set("z", location.getBlockZ());
        writeInventory(config, "filters", inventory.getInventory(), 0, QuarryCacheInventory.FILTER_SLOTS, slot -> false);
        writeInventory(config, "storage", inventory.getInventory(), QuarryCacheInventory.STORAGE_START,
                QuarryCacheInventory.TOTAL_SLOTS, inventory::isSeparatorSlot);

        try {
            config.save(file);
        } catch (IOException error) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to save quarry cache at " + location, error);
            }
        }
    }

    QuarryCacheContents load(Location location) {
        if (baseDir == null) {
            return QuarryCacheContents.empty();
        }

        File file = fileFor(location);
        if (!file.exists()) {
            return QuarryCacheContents.empty();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<Integer, ItemStack> slots = new HashMap<>();
        readInventory(config, "filters", slots);
        readInventory(config, "storage", slots);
        return new QuarryCacheContents(slots);
    }

    void delete(Location location) {
        if (baseDir == null) {
            return;
        }

        File file = fileFor(location);
        if (file.exists() && !file.delete() && plugin != null) {
            plugin.getLogger().warning("Failed to delete quarry cache storage file: " + file);
        }
    }

    boolean hasStoredContents(ItemStack item) {
        if (item == null || item.getType().isAir() || nbtService == null) {
            return false;
        }
        String encoded = nbtService.readItem(item, nbt -> nbt.getString(NBT_KEY));
        return encoded != null && !encoded.isEmpty();
    }

    void writeToItem(ItemStack item, QuarryCacheInventory inventory) {
        if (item == null || item.getType().isAir() || nbtService == null) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        writeInventory(config, "filters", inventory.getInventory(), 0, QuarryCacheInventory.FILTER_SLOTS, slot -> false);
        writeInventory(config, "storage", inventory.getInventory(), QuarryCacheInventory.STORAGE_START,
                QuarryCacheInventory.TOTAL_SLOTS, inventory::isSeparatorSlot);

        String encoded = config.saveToString();
        nbtService.editItem(item, nbt -> nbt.setString(NBT_KEY, encoded));
    }

    void applyToInventory(ItemStack item, QuarryCacheInventory inventory) {
        if (item == null || nbtService == null) {
            return;
        }

        String encoded = nbtService.readItem(item, nbt -> nbt.getString(NBT_KEY));
        if (encoded == null || encoded.isEmpty()) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(encoded);
        } catch (Exception error) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to read quarry cache item contents", error);
            }
            return;
        }

        applyContents(inventory, readContents(config));
    }

    void applyContents(QuarryCacheInventory inventory, QuarryCacheContents contents) {
        Inventory backing = inventory.getInventory();
        for (Map.Entry<Integer, ItemStack> entry : contents.copySlots().entrySet()) {
            if (inventory.isSeparatorSlot(entry.getKey())) {
                continue;
            }
            backing.setItem(entry.getKey(), entry.getValue());
        }
        inventory.restoreDecorations();
    }

    private QuarryCacheContents readContents(YamlConfiguration config) {
        Map<Integer, ItemStack> slots = new HashMap<>();
        readInventory(config, "filters", slots);
        readInventory(config, "storage", slots);
        return new QuarryCacheContents(slots);
    }

    private void writeInventory(YamlConfiguration config, String path, Inventory inventory, int start, int end,
                                  SlotPredicate skip) {
        for (int slot = start; slot < end; slot++) {
            if (skip.test(slot)) {
                continue;
            }
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            config.set(path + "." + slot, item.serialize());
        }
    }

    @SuppressWarnings("unchecked")
    private void readInventory(YamlConfiguration config, String path, Map<Integer, ItemStack> slots) {
        if (!config.isConfigurationSection(path)) {
            return;
        }

        for (String slotKey : config.getConfigurationSection(path).getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotKey);
                Object raw = config.get(path + "." + slotKey);
                if (raw instanceof Map<?, ?> serialized) {
                    ItemStack item = ItemStack.deserialize((Map<String, Object>) serialized);
                    if (item != null && !item.getType().isAir()) {
                        slots.put(slot, item);
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private File fileFor(Location location) {
        return new File(new File(baseDir, requireWorldName(location)),
                location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ() + ".yml");
    }

    private static String requireWorldName(Location location) {
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Location world must not be null");
        }
        return location.getWorld().getName();
    }

    @FunctionalInterface
    private interface SlotPredicate {
        boolean test(int slot);
    }
}
