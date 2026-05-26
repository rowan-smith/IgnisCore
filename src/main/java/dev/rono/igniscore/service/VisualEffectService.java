package dev.rono.igniscore.service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service for handling advanced visual effects, using ProtocolLib when available.
 */
public class VisualEffectService {
    private final ProtocolService protocolService;

    public VisualEffectService(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    /**
     * Plays a fake explosion at the given location for specific players.
     */
    public void playFakeExplosion(Location location, float power, Collection<Player> players) {
        if (protocolService.isEnabled()) {
            ProtocolManager pm = protocolService.getProtocolManager();
            PacketContainer packet = pm.createPacket(PacketType.Play.Server.EXPLOSION);
            
            packet.getDoubles().write(0, location.getX());
            packet.getDoubles().write(1, location.getY());
            packet.getDoubles().write(2, location.getZ());
            packet.getFloat().write(0, power);
            
            // Empty list of records (affected blocks)
            packet.getSpecificModifier(List.class).write(0, new ArrayList<>());
            
            // Player motion (0, 0, 0)
            packet.getFloat().write(1, 0f);
            packet.getFloat().write(2, 0f);
            packet.getFloat().write(3, 0f);

            for (Player player : players) {
                try {
                    pm.sendServerPacket(player, packet);
                } catch (Exception e) {
                    // Fail gracefully
                }
            }
        } else {
            // Fallback to standard Bukkit API
            for (Player player : players) {
                player.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
            }
        }
    }

    /**
     * Shows a holographic block preview to a player.
     */
    public void showBlockPreview(Player player, Location location, Material material) {
        // Even without ProtocolLib, we can use this standard Bukkit method
        // But for "illusion" layer, we could use packets to avoid any server-side state
        player.sendBlockChange(location, material.createBlockData());
    }
}
