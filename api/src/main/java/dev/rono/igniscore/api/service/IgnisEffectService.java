package dev.rono.igniscore.api.service;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * High-level visual and audio effects for strategies and extensions.
 */
public interface IgnisEffectService {

    void playSound(Location location, String soundName, float volume, float pitch);

    void playFakeExplosion(Location location, float power, Collection<Player> players);

    void showBlockPreview(Player player, Location location, Material material);
}
