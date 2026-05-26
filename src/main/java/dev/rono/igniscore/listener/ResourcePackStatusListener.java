package dev.rono.igniscore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;

public class ResourcePackStatusListener implements Listener {
    private final Plugin plugin;

    public ResourcePackStatusListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        plugin.getLogger().info("Player " + event.getPlayer().getName()
                + " resource pack status: " + event.getStatus());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            plugin.getLogger().warning("Resource pack download failed for " + event.getPlayer().getName());
        }
    }
}
