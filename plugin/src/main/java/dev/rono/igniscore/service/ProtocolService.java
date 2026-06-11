package dev.rono.igniscore.service;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Service to handle ProtocolLib integration.
 * Gracefully handles cases where ProtocolLib is not installed.
 */
public class ProtocolService {
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

    /**
     * @return true if ProtocolLib is available and functional.
     */
    public boolean isEnabled() {
        return enabled && protocolManager != null;
    }

    /**
     * @return the ProtocolManager, or null if ProtocolLib is not enabled.
     */
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
