package dev.rono.extensions.shared.gui;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers chest-style block inventories with file persistence.
 */
public final class BlockStorageRegistry {
    private final ExtensionSupport extensionSupport;
    private final BlockStoragePersistence persistence;
    private final Map<IgnisLocation, BlockStorageGui> blockGuis = new ConcurrentHashMap<>();
    private final Map<String, BlockStorageGui> playerGuis = new ConcurrentHashMap<>();

    public BlockStorageRegistry(IgnisStrategyContext context, String namespace) {
        this.extensionSupport = context.extensions();
        this.persistence = new BlockStoragePersistence(extensionSupport, namespace);
    }

    public void registerBlock(IgnisLocation location, Component title, int rows) {
        IgnisLocation block = Locations.toBlock(location);
        unregister(block);

        BlockStorageGui gui = new BlockStorageGui(block, title, extensionSupport, rows);
        persistence.apply(BlockStoragePersistence.blockKey(block), gui.inventory(), 0, gui.inventory().getSize());
        gui.setOnChanged(g -> persistence.save(
                BlockStoragePersistence.blockKey(block),
                g.inventory(),
                0,
                g.inventory().getSize()));
        gui.register();
        blockGuis.put(block, gui);
    }

    public void registerPerPlayer(IgnisLocation location, Component title, int rows) {
        IgnisLocation block = Locations.toBlock(location);
        blockGuis.put(block, null);
    }

    public void openBlock(IgnisPlayer player, IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        BlockStorageGui gui = blockGuis.get(block);
        if (gui == null) {
            return;
        }
        gui.open(player);
    }

    public void openPerPlayer(IgnisPlayer player, IgnisLocation location, Component title, int rows) {
        IgnisLocation block = Locations.toBlock(location);
        UUID playerId = player.getUniqueId();
        String key = BlockStoragePersistence.playerKey(block, playerId);
        BlockStorageGui gui = playerGuis.computeIfAbsent(key, ignored -> {
            BlockStorageGui created = new BlockStorageGui(block, title, extensionSupport, rows);
            persistence.apply(key, created.inventory(), 0, created.inventory().getSize());
            created.setOnChanged(g -> persistence.save(key, g.inventory(), 0, g.inventory().getSize()));
            created.register();
            return created;
        });
        gui.open(player);
    }

    public BlockStorageGui blockGui(IgnisLocation location) {
        return blockGuis.get(Locations.toBlock(location));
    }

    public void unregister(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        BlockStorageGui gui = blockGuis.remove(block);
        if (gui != null) {
            persistence.save(
                    BlockStoragePersistence.blockKey(block),
                    gui.inventory(),
                    0,
                    gui.inventory().getSize());
            gui.unregister();
        }
        String prefix = BlockStoragePersistence.blockKey(block) + "/player/";
        playerGuis.entrySet().removeIf(entry -> {
            if (!entry.getKey().startsWith(prefix)) {
                return false;
            }
            BlockStorageGui playerGui = entry.getValue();
            persistence.save(entry.getKey(), playerGui.inventory(), 0, playerGui.inventory().getSize());
            playerGui.unregister();
            return true;
        });
    }
}
