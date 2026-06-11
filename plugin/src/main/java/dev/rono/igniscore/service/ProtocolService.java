package dev.rono.igniscore.service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service to handle ProtocolLib integration.
 * Gracefully handles cases where ProtocolLib is not installed.
 */
public class ProtocolService implements IgnisProtocolService {
    private final boolean enabled;
    private ProtocolManager protocolManager;

    @Inject
    public ProtocolService(Plugin plugin) {
        Logger logger = plugin.getLogger();
        boolean isPresent = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
        boolean active = false;

        if (isPresent) {
            try {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                active = true;
                logger.info("ProtocolLib integration enabled.");
            } catch (Throwable t) {
                logger.warning("Failed to initialize ProtocolLib integration: " + t.getMessage());
            }
        } else {
            logger.info("ProtocolLib not found. Advanced visual features will be disabled.");
        }
        this.enabled = active;
    }

    @Override
    public boolean isEnabled() {
        return enabled && protocolManager != null;
    }

    /**
     * @return the ProtocolManager, or null if ProtocolLib is not enabled.
     */
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void sendFakeExplosion(Location location, float power, Collection<Player> players) {
        if (isEnabled()) {
            ProtocolManager pm = protocolManager;
            PacketContainer packet = pm.createPacket(PacketType.Play.Server.EXPLOSION);

            packet.getDoubles().write(0, location.getX());
            packet.getDoubles().write(1, location.getY());
            packet.getDoubles().write(2, location.getZ());
            packet.getFloat().write(0, power);

            packet.getSpecificModifier(List.class).write(0, new ArrayList<>());

            packet.getFloat().write(1, 0f);
            packet.getFloat().write(2, 0f);
            packet.getFloat().write(3, 0f);

            for (Player player : players) {
                try {
                    pm.sendServerPacket(player, packet);
                } catch (Exception ignored) {
                }
            }
        } else {
            for (Player player : players) {
                player.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
            }
        }
    }
}
