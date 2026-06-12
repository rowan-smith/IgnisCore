package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Material;

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

    public void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
        protocolService.sendFakeExplosion(location, power, players);
    }

    public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
        Material material = Material.matchMaterial(materialKey);
        if (material == null) {
            material = Material.STONE;
        }
        BukkitBridge.unwrap(player).sendBlockChange(BukkitBridge.toBukkit(location), material.createBlockData());
    }
}
