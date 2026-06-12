package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

final class QuarryCacheStorage {
    static final String PORTABLE_ID_KEY = "ignis:quarry_cache_portable_id";
    static final String INLINE_CONTENTS_KEY = "ignis:quarry_cache_contents";

    private final ExtensionSupport extensionSupport;
    private final IgnisNbtService nbtService;
    private final Path baseDir;
    private final Path portableDir;
    private final Logger logger;

    QuarryCacheStorage(ExtensionSupport extensionSupport, IgnisNbtService nbtService) {
        this.extensionSupport = extensionSupport;
        this.nbtService = nbtService;
        Path dataDirectory = extensionSupport.getDataDirectory();
        this.baseDir = dataDirectory == null ? null : dataDirectory.resolve("quarry-cache");
        this.portableDir = baseDir == null ? null : baseDir.resolve("portable");
        this.logger = Logger.getLogger(QuarryCacheStorage.class.getName());
    }

    void save(IgnisLocation location, QuarryCacheInventory inventory) {
        if (baseDir == null) {
            return;
        }

        var file = fileFor(location);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, encodeContents(readInventoryContents(inventory)), StandardCharsets.UTF_8);
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to save quarry cache at " + location, error);
        }
    }

    QuarryCacheContents load(IgnisLocation location) {
        if (baseDir == null) {
            return QuarryCacheContents.empty();
        }

        var file = fileFor(location);
        if (!Files.exists(file)) {
            return QuarryCacheContents.empty();
        }

        try {
            return decodeContents(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to load quarry cache at " + location, error);
            return QuarryCacheContents.empty();
        }
    }

    void delete(IgnisLocation location) {
        if (baseDir == null) {
            return;
        }

        var file = fileFor(location);
        try {
            Files.deleteIfExists(file);
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to delete quarry cache storage file: " + file, error);
        }
    }

    void attachContentsToItem(IgnisItem item, QuarryCacheInventory inventory) {
        attachContentsToItem(item, readInventoryContents(inventory));
    }

    void attachContentsToItem(IgnisItem item, QuarryCacheContents contents) {
        if (item == null || item.isAir() || contents.isEmpty()) {
            return;
        }

        var portableId = savePortable(contents);
        if (portableId == null) {
            writeInlineContents(item, contents);
            return;
        }

        nbtService.setItemString(item, PORTABLE_ID_KEY, portableId);
        writeInlineContents(item, contents);
    }

    void restoreFromItem(IgnisItem item, QuarryCacheInventory inventory) {
        if (item == null || item.isAir()) {
            return;
        }

        var portableId = nbtService.getItemString(item, PORTABLE_ID_KEY);
        if (portableId != null && !portableId.isBlank()) {
            var portableContents = loadPortable(portableId);

            if (!portableContents.isEmpty()) {
                applyContents(inventory, portableContents);
                deletePortable(portableId);
                nbtService.setItemString(item, PORTABLE_ID_KEY, "");
                nbtService.setItemString(item, INLINE_CONTENTS_KEY, "");
                return;
            }
        }

        applyInlineContents(item, inventory);
    }

    boolean hasStoredContents(IgnisItem item) {
        if (item == null || item.isAir() || nbtService == null) {
            return false;
        }

        var portableId = nbtService.getItemString(item, PORTABLE_ID_KEY);
        if (portableId != null && !portableId.isBlank() && portableFile(portableId).toFile().exists()) {
            return true;
        }

        var encoded = nbtService.getItemString(item, INLINE_CONTENTS_KEY);
        return encoded != null && !encoded.isBlank();
    }

    void applyContents(QuarryCacheInventory inventory, QuarryCacheContents contents) {
        for (Map.Entry<Integer, QuarryCacheContents.StoredItem> entry : contents.copySlots().entrySet()) {
            int slot = resolveInventorySlot(entry.getKey());
            if (inventory.isSeparatorSlot(slot)) {
                continue;
            }

            IgnisItem item = entry.getValue().toItem(inventory);
            if (item != null) {
                inventory.getInventory().setItem(slot, item);
            }
        }

        inventory.restoreDecorations();
    }

    private QuarryCacheContents readInventoryContents(QuarryCacheInventory inventory) {
        var slots = new HashMap<Integer, QuarryCacheContents.StoredItem>();
        collectFilterSlots(slots, inventory);
        collectInventorySlots(slots, inventory, QuarryCacheInventory.STORAGE_START, QuarryCacheInventory.TOTAL_SLOTS, inventory::isSeparatorSlot);
        return new QuarryCacheContents(slots);
    }

    private void collectInventorySlots(Map<Integer, QuarryCacheContents.StoredItem> slots, QuarryCacheInventory inventory,
                                       int start, int end, SlotPredicate skip) {
        for (int slot = start; slot < end; slot++) {
            if (skip.test(slot)) {
                continue;
            }

            var item = inventory.getInventory().getItem(slot);
            var stored = QuarryCacheContents.StoredItem.from(item);
            if (stored != null) {
                slots.put(slot, stored);
            }
        }
    }

    private void collectFilterSlots(Map<Integer, QuarryCacheContents.StoredItem> slots, QuarryCacheInventory inventory) {
        for (int i = 0; i < QuarryCacheInventory.FILTER_SLOTS; i++) {
            var item = inventory.getInventory().getItem(QuarryCacheInventory.FILTER_START + i);
            var stored = QuarryCacheContents.StoredItem.from(item);
            if (stored != null) {
                slots.put(i, stored);
            }
        }
    }

    private String savePortable(QuarryCacheContents contents) {
        if (portableDir == null) {
            return null;
        }

        try {
            Files.createDirectories(portableDir);
        } catch (IOException error) {
            logger.log(Level.WARNING, "Could not create quarry cache portable directory: " + portableDir, error);
            return null;
        }

        var portableId = UUID.randomUUID().toString();
        var file = portableFile(portableId);

        try {
            Files.writeString(file, encodeContents(contents), StandardCharsets.UTF_8);
            return portableId;
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to save portable quarry cache " + portableId, error);
            return null;
        }
    }

    private QuarryCacheContents loadPortable(String portableId) {
        if (portableDir == null || portableId == null || portableId.isBlank()) {
            return QuarryCacheContents.empty();
        }

        var file = portableFile(portableId);
        if (!Files.exists(file)) {
            return QuarryCacheContents.empty();
        }

        try {
            return decodeContents(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to load portable quarry cache " + portableId, error);
            return QuarryCacheContents.empty();
        }
    }

    private void deletePortable(String portableId) {
        if (portableDir == null || portableId == null || portableId.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(portableFile(portableId));
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to delete portable quarry cache file: " + portableId, error);
        }
    }

    private void writeInlineContents(IgnisItem item, QuarryCacheContents contents) {
        if (nbtService == null) {
            return;
        }
        nbtService.setItemString(item, INLINE_CONTENTS_KEY, encodeContents(contents));
    }

    private void applyInlineContents(IgnisItem item, QuarryCacheInventory inventory) {
        if (nbtService == null) {
            return;
        }

        var encoded = nbtService.getItemString(item, INLINE_CONTENTS_KEY);
        if (encoded == null || encoded.isBlank()) {
            return;
        }

        applyContents(inventory, decodeContents(encoded));
        nbtService.setItemString(item, INLINE_CONTENTS_KEY, "");
    }

    private static int resolveInventorySlot(int persistedSlot) {
        if (persistedSlot < QuarryCacheInventory.FILTER_SLOTS) {
            return QuarryCacheInventory.FILTER_START + persistedSlot;
        }
        return persistedSlot;
    }

    private static String encodeContents(QuarryCacheContents contents) {
        StringBuilder builder = new StringBuilder();
        for (var entry : contents.copySlots().entrySet()) {
            builder.append("slot.")
                    .append(entry.getKey())
                    .append('=')
                    .append(entry.getValue().materialKey())
                    .append(':')
                    .append(entry.getValue().amount())
                    .append('\n');
        }
        return builder.toString();
    }

    private static QuarryCacheContents decodeContents(String encoded) {
        var slots = new HashMap<Integer, QuarryCacheContents.StoredItem>();
        for (String line : encoded.split("\\R")) {
            if (!line.startsWith("slot.")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 5) {
                continue;
            }
            int slot = Integer.parseInt(line.substring(5, separator));
            String[] parts = line.substring(separator + 1).split(":");
            if (parts.length != 2) {
                continue;
            }
            slots.put(slot, new QuarryCacheContents.StoredItem(parts[0], Integer.parseInt(parts[1])));
        }
        return new QuarryCacheContents(slots);
    }

    private Path fileFor(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        return baseDir.resolve(block.worldName())
                .resolve(block.x() + "_" + block.y() + "_" + block.z() + ".txt");
    }

    private Path portableFile(String portableId) {
        return portableDir.resolve(portableId + ".txt");
    }

    @FunctionalInterface
    private interface SlotPredicate {
        boolean test(int slot);
    }
}
