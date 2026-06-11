package dev.rono.igniscore.api.service;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Low-level protocol integration for advanced client-side effects.
 * Implementations may use ProtocolLib when available.
 */
public interface IgnisProtocolService {

    boolean isEnabled();

    void sendFakeExplosion(Location location, float power, Collection<Player> players);
}
