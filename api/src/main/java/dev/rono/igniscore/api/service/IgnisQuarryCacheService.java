package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.model.BlockDefinition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Storage and collection service for Quarry Cache blocks.
 */
public interface IgnisQuarryCacheService {

    void register(Location location, BlockDefinition definition);

    void unregister(Location location);

    boolean isCache(Location location);

    void openGui(Player player, Location location);

    void dropContents(Location location);
}
