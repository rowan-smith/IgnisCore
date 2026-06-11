package dev.rono.igniscore.service;

import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Service for handling advanced visual effects, using ProtocolLib when available.
 */
public class VisualEffectService {
    private final ProtocolService protocolService;

    @Inject
    public VisualEffectService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    /**
     * Plays a fake explosion at the given location for specific players.
     */
    public void playFakeExplosion(Location location, float power, Collection<Player> players) {
        protocolService.sendFakeExplosion(location, power, players);
    }

    /**
     * Shows a holographic block preview to a player.
     */
    public void showBlockPreview(Player player, Location location, Material material) {
        player.sendBlockChange(location, material.createBlockData());
    }
}
