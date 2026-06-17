package dev.rono.extensions.shared.gui;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages secure trade sessions per placed trade table.
 */
public final class SecureTradeRegistry {
    private final ExtensionSupport extensionSupport;
    private final BlockStoragePersistence persistence;
    private final Map<IgnisLocation, SecureTradeGui> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, IgnisLocation> openPlayers = new ConcurrentHashMap<>();

    public SecureTradeRegistry(IgnisStrategyContext context) {
        this.extensionSupport = context.extensions();
        this.persistence = new BlockStoragePersistence(extensionSupport, "secure-trade");
    }

    public void register(IgnisLocation location, Component title) {
        IgnisLocation block = Locations.toBlock(location);
        unregister(block);
        SecureTradeGui gui = new SecureTradeGui(block, title, extensionSupport);
        persistence.apply(BlockStoragePersistence.blockKey(block), gui.inventory(), 0, SecureTradeGui.TOTAL_SLOTS);
        gui.setOnChanged(g -> {
            persistence.save(
                    BlockStoragePersistence.blockKey(block),
                    g.inventory(),
                    0,
                    SecureTradeGui.TOTAL_SLOTS);
            if (g.bothConfirmed()) {
                g.executeTrade(extensionSupport);
            }
        });
        gui.register();
        sessions.put(block, gui);
    }

    public void open(IgnisPlayer player, IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        SecureTradeGui gui = sessions.get(block);
        if (gui == null) {
            return;
        }
        openPlayers.put(player.getUniqueId(), block);
        gui.open(player);
    }

    public void onClose(IgnisPlayer player) {
        UUID playerId = player.getUniqueId();
        IgnisLocation block = openPlayers.remove(playerId);
        if (block == null) {
            return;
        }
        SecureTradeGui gui = sessions.get(block);
        if (gui == null) {
            return;
        }
        if (gui.bothConfirmed()) {
            for (IgnisItem received : gui.offerFor(playerId)) {
                giveOrDrop(player, received);
            }
            player.sendMessage("<green>Trade completed.</green>");
        } else {
            for (IgnisItem returned : gui.takeOffer(playerId)) {
                giveOrDrop(player, returned);
            }
        }
    }

    public void unregister(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        SecureTradeGui gui = sessions.remove(block);
        if (gui != null) {
            persistence.save(
                    BlockStoragePersistence.blockKey(block),
                    gui.inventory(),
                    0,
                    SecureTradeGui.TOTAL_SLOTS);
            gui.unregister();
        }
        openPlayers.entrySet().removeIf(entry -> entry.getValue().equals(block));
    }

    private void giveOrDrop(IgnisPlayer player, IgnisItem item) {
        if (item == null || item.isAir()) {
            return;
        }
        player.sendMessage("<gray>Received <white>" + item.getAmount() + "x " + item.getMaterialKey() + "</white>.</gray>");
    }
}
