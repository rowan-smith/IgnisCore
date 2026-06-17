package dev.rono.extensions.shared.gui;

import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-backed slot persistence for utility block inventories.
 */
public final class BlockStoragePersistence {
    private final ExtensionSupport extensionSupport;
    private final Path baseDir;
    private final Logger logger;

    public BlockStoragePersistence(ExtensionSupport extensionSupport, String namespace) {
        this.extensionSupport = extensionSupport;
        Path dataDirectory = extensionSupport.getDataDirectory();
        this.baseDir = dataDirectory == null ? null : dataDirectory.resolve(namespace);
        this.logger = Logger.getLogger(BlockStoragePersistence.class.getName() + "." + namespace);
    }

    public void save(String key, IgnisInventory inventory, int slotStart, int slotEnd) {
        if (baseDir == null) {
            return;
        }
        Path file = fileFor(key);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, encode(inventory, slotStart, slotEnd), StandardCharsets.UTF_8);
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to save storage " + key, error);
        }
    }

    public void apply(String key, IgnisInventory inventory, int slotStart, int slotEnd) {
        Map<Integer, StoredItem> slots = load(key);
        for (Map.Entry<Integer, StoredItem> entry : slots.entrySet()) {
            int slot = slotStart + entry.getKey();
            if (slot < slotStart || slot >= slotEnd) {
                continue;
            }
            IgnisItem item = entry.getValue().toItem(extensionSupport);
            if (item != null) {
                inventory.setItem(slot, item);
            }
        }
    }

    public void delete(String key) {
        if (baseDir == null) {
            return;
        }
        try {
            Files.deleteIfExists(fileFor(key));
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to delete storage " + key, error);
        }
    }

    public static String blockKey(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        return block.worldName() + "/" + block.x() + "_" + block.y() + "_" + block.z();
    }

    public static String playerKey(IgnisLocation location, java.util.UUID playerId) {
        return blockKey(location) + "/player/" + playerId;
    }

    private Map<Integer, StoredItem> load(String key) {
        if (baseDir == null) {
            return Map.of();
        }
        Path file = fileFor(key);
        if (!Files.exists(file)) {
            return Map.of();
        }
        try {
            return decode(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException error) {
            logger.log(Level.WARNING, "Failed to load storage " + key, error);
            return Map.of();
        }
    }

    private Path fileFor(String key) {
        return baseDir.resolve(key + ".txt");
    }

    private static String encode(IgnisInventory inventory, int slotStart, int slotEnd) {
        StringBuilder builder = new StringBuilder();
        for (int slot = slotStart; slot < slotEnd; slot++) {
            IgnisItem item = inventory.getItem(slot);
            if (item == null || item.isAir()) {
                continue;
            }
            builder.append("slot.")
                    .append(slot - slotStart)
                    .append('=')
                    .append(item.getMaterialKey())
                    .append(':')
                    .append(item.getAmount())
                    .append('\n');
        }
        return builder.toString();
    }

    private static Map<Integer, StoredItem> decode(String encoded) {
        Map<Integer, StoredItem> slots = new HashMap<>();
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
            slots.put(slot, new StoredItem(parts[0], Integer.parseInt(parts[1])));
        }
        return slots;
    }

    private record StoredItem(String materialKey, int amount) {
        IgnisItem toItem(ExtensionSupport extensionSupport) {
            if (materialKey == null || materialKey.isBlank() || amount <= 0) {
                return null;
            }
            return extensionSupport.createItem(materialKey, amount);
        }
    }
}
