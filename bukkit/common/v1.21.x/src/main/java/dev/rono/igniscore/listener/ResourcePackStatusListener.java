package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackStatusListener implements Listener {
    private final Main plugin;

    @Inject
    public ResourcePackStatusListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        plugin.debug("Player " + event.getPlayer().getName()
                + " resource pack status: " + event.getStatus());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            plugin.getLogger().warning("Resource pack download failed for " + event.getPlayer().getName());
        }
    }
}
