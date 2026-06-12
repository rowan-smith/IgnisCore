package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

final class QuarryCacheStorage {
    static final String PORTABLE_ID_KEY = "ignis:quarry_cache_portable_id";
    static final String INLINE_CONTENTS_KEY = "ignis:quarry_cache_contents";

    private final Plugin plugin;
    private final IgnisNbtService nbtService;
    private final File baseDir;
    private final File portableDir;

    QuarryCacheStorage(Plugin plugin, IgnisNbtService nbtService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
        this.baseDir = plugin == null ? null : new File(plugin.getDataFolder(), "quarry-cache");
        this.portableDir = baseDir == null ? null : new File(baseDir, "portable");
    }

    void save(Location location, QuarryCacheInventory inventory) {
        if (baseDir == null) {
            return;
        }

        var file = fileFor(location);
        var parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().warning("Could not create quarry cache storage directory: " + parent);
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("world", requireWorldName(location));
        config.set("x", location.getBlockX());
        config.set("y", location.getBlockY());
        config.set("z", location.getBlockZ());

        writeFilterSection(config, "filters", inventory.getInventory());
        writeInventory(config, "storage", inventory.getInventory(), QuarryCacheInventory.STORAGE_START, QuarryCacheInventory.TOTAL_SLOTS, inventory::isSeparatorSlot);

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

        var file = fileFor(location);
        if (!file.exists()) {
            return QuarryCacheContents.empty();
        }

        return readContents(YamlConfiguration.loadConfiguration(file));
    }

    void delete(Location location) {
        if (baseDir == null) {
            return;
        }

        var file = fileFor(location);
        if (file.exists() && !file.delete() && plugin != null) {
            plugin.getLogger().warning("Failed to delete quarry cache storage file: " + file);
        }
    }

    void attachContentsToItem(ItemStack item, QuarryCacheInventory inventory) {
        attachContentsToItem(item, inventory.getInventory(), inventory::isSeparatorSlot);
    }

    void attachContentsToItem(ItemStack item, QuarryCacheContents contents) {
        if (item == null || item.getType().isAir() || contents.isEmpty()) {
            return;
        }

        var portableId = savePortable(contents);
        if (portableId == null) {
            return;
        }

        writePortableId(item, portableId);
    }

    void restoreFromItem(ItemStack item, QuarryCacheInventory inventory) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        var portableId = readPortableId(item);
        if (portableId != null) {
            var portableContents = loadPortable(portableId);

            if (!portableContents.isEmpty()) {
                applyContents(inventory, portableContents);
                deletePortable(portableId);
                clearPortableId(item);
                clearInlineContents(item);
                return;
            }
        }

        applyInlineContents(item, inventory);
    }

    boolean hasStoredContents(ItemStack item) {
        if (item == null || item.getType().isAir() || nbtService == null) {
            return false;
        }

        var portableId = readPortableId(item);
        if (portableId != null && portableFile(portableId).exists()) {
            return true;
        }

        var encoded = nbtService.readItem(item, nbt -> nbt.getString(INLINE_CONTENTS_KEY));
        return encoded != null && !encoded.isEmpty();
    }

    void applyContents(QuarryCacheInventory inventory, QuarryCacheContents contents) {
        var backing = inventory.getInventory();

        for (Map.Entry<Integer, ItemStack> entry : contents.copySlots().entrySet()) {
            int slot = resolveInventorySlot(entry.getKey());
            if (inventory.isSeparatorSlot(slot)) {
                continue;
            }

            backing.setItem(slot, entry.getValue());
        }

        inventory.restoreDecorations();
    }

    private void attachContentsToItem(ItemStack item, Inventory source, SlotPredicate skipSeparator) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        var contents = readInventoryContents(source, skipSeparator);
        if (contents.isEmpty()) {
            clearPortableId(item);
            clearInlineContents(item);
            return;
        }

        var portableId = savePortable(contents);
        if (portableId == null) {
            writeInlineContents(item, source, skipSeparator);
            return;
        }

        writePortableId(item, portableId);
        writeInlineContents(item, source, skipSeparator);
    }

    private String savePortable(QuarryCacheContents contents) {
        if (portableDir == null) {
            return null;
        }

        if (!portableDir.exists() && !portableDir.mkdirs()) {
            if (plugin != null) {
                plugin.getLogger().warning("Could not create quarry cache portable directory: " + portableDir);
            }

            return null;
        }

        var portableId = UUID.randomUUID().toString();
        var file = portableFile(portableId);
        var config = new YamlConfiguration();

        writeContentsMap(config, contents.copySlots());

        try {
            config.save(file);
            return portableId;

        } catch (IOException error) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to save portable quarry cache " + portableId, error);
            }

            return null;
        }
    }

    private QuarryCacheContents loadPortable(String portableId) {
        if (portableDir == null || portableId == null || portableId.isBlank()) {
            return QuarryCacheContents.empty();
        }

        var file = portableFile(portableId);
        if (!file.exists()) {
            return QuarryCacheContents.empty();
        }

        return readContents(YamlConfiguration.loadConfiguration(file));
    }

    private void deletePortable(String portableId) {
        if (portableDir == null || portableId == null || portableId.isBlank()) {
            return;
        }

        var file = portableFile(portableId);
        if (file.exists() && !file.delete() && plugin != null) {
            plugin.getLogger().warning("Failed to delete portable quarry cache file: " + file);
        }
    }

    private void writePortableId(ItemStack item, String portableId) {
        if (nbtService == null) {
            return;
        }

        nbtService.editItem(item, nbt -> nbt.setString(PORTABLE_ID_KEY, portableId));
    }

    private String readPortableId(ItemStack item) {
        if (nbtService == null || item == null || item.getType().isAir()) {
            return null;
        }

        return nbtService.readItem(item, nbt -> nbt.getString(PORTABLE_ID_KEY));
    }

    private void clearPortableId(ItemStack item) {
        if (nbtService == null || item == null || item.getType().isAir()) {
            return;
        }

        nbtService.editItem(item, nbt -> nbt.removeKey(PORTABLE_ID_KEY));
    }

    private void writeInlineContents(ItemStack item, Inventory source, SlotPredicate skipSeparator) {
        if (nbtService == null) {
            return;
        }

        var config = new YamlConfiguration();
        writeFilterSection(config, "filters", source);
        writeInventory(config, "storage", source, QuarryCacheInventory.STORAGE_START, QuarryCacheInventory.TOTAL_SLOTS, skipSeparator::test);

        var encoded = config.saveToString();
        nbtService.editItem(item, nbt -> nbt.setString(INLINE_CONTENTS_KEY, encoded));
    }

    private void applyInlineContents(ItemStack item, QuarryCacheInventory inventory) {
        if (nbtService == null) {
            return;
        }

        var encoded = nbtService.readItem(item, nbt -> nbt.getString(INLINE_CONTENTS_KEY));
        if (encoded == null || encoded.isEmpty()) {
            return;
        }

        var config = new YamlConfiguration();
        try {
            config.loadFromString(encoded);

        } catch (Exception error) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to read inline quarry cache item contents", error);
            }

            return;
        }

        applyContents(inventory, readContents(config));
        clearInlineContents(item);
    }

    private void clearInlineContents(ItemStack item) {
        if (nbtService == null || item == null || item.getType().isAir()) {
            return;
        }

        nbtService.editItem(item, nbt -> nbt.removeKey(INLINE_CONTENTS_KEY));
    }

    private QuarryCacheContents readInventoryContents(Inventory inventory, SlotPredicate skipSeparator) {
        var slots = new HashMap<Integer, ItemStack>();

        collectFilterSlots(slots, inventory);
        collectInventorySlots(slots, inventory, QuarryCacheInventory.STORAGE_START, QuarryCacheInventory.TOTAL_SLOTS, skipSeparator::test);

        return new QuarryCacheContents(slots);
    }

    private void collectInventorySlots(Map<Integer, ItemStack> slots, Inventory inventory, int start, int end, SlotPredicate skip) {
        for (int slot = start; slot < end; slot++) {
            if (skip.test(slot)) {
                continue;
            }

            var item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }

            slots.put(slot, item.clone());
        }
    }

    private void writeContentsMap(YamlConfiguration config, Map<Integer, ItemStack> slots) {
        for (var entry : slots.entrySet()) {
            var section = entry.getKey() < QuarryCacheInventory.STORAGE_START ? "filters" : "storage";
            config.set(section + "." + entry.getKey(), entry.getValue().serialize());
        }
    }

    private static int resolveInventorySlot(int persistedSlot) {
        if (persistedSlot < QuarryCacheInventory.FILTER_SLOTS) {
            return QuarryCacheInventory.FILTER_START + persistedSlot;
        }
        return persistedSlot;
    }

    private void writeFilterSection(YamlConfiguration config, String path, Inventory inventory) {
        for (int i = 0; i < QuarryCacheInventory.FILTER_SLOTS; i++) {
            var item = inventory.getItem(QuarryCacheInventory.FILTER_START + i);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            config.set(path + "." + i, item.serialize());
        }
    }

    private void collectFilterSlots(Map<Integer, ItemStack> slots, Inventory inventory) {
        for (int i = 0; i < QuarryCacheInventory.FILTER_SLOTS; i++) {
            var item = inventory.getItem(QuarryCacheInventory.FILTER_START + i);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            slots.put(i, item.clone());
        }
    }

    private QuarryCacheContents readContents(YamlConfiguration config) {
        var slots = new HashMap<Integer, ItemStack>();

        readInventory(config, "filters", slots);
        readInventory(config, "storage", slots);

        return new QuarryCacheContents(slots);
    }

    private void writeInventory(YamlConfiguration config, String path, Inventory inventory, int start, int end, SlotPredicate skip) {
        for (int slot = start; slot < end; slot++) {
            if (skip.test(slot)) {
                continue;
            }

            var item = inventory.getItem(slot);
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

        for (var slotKey : config.getConfigurationSection(path).getKeys(false)) {
            try {
                var slot = Integer.parseInt(slotKey);
                var serialized = readSerializedItem(config.get(path + "." + slotKey));
                if (serialized == null) {
                    continue;
                }

                var item = ItemStack.deserialize(serialized);
                if (item != null && !item.getType().isAir()) {
                    slots.put(slot, item);
                }

            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> readSerializedItem(Object raw) {
        if (raw instanceof ConfigurationSection section) {
            return section.getValues(false);
        }

        if (raw instanceof Map<?, ?> serialized) {
            return (Map<String, Object>) serialized;
        }

        return null;
    }

    private File fileFor(Location location) {
        return new File(new File(baseDir, requireWorldName(location)), location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ() + ".yml");
    }

    private File portableFile(String portableId) {
        return new File(portableDir, portableId + ".yml");
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
