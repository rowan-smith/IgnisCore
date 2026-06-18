package dev.rono.igniscore.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.IgnisPluginContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackStatusListener implements Listener {
    private final IgnisPluginContext pluginContext;

    @Inject
    public ResourcePackStatusListener(IgnisPluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        pluginContext.debug("Player " + event.getPlayer().getName()
                + " resource pack status: " + event.getStatus());

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            pluginContext.plugin().getLogger().warning("Resource pack download failed for " + event.getPlayer().getName());
        }
    }
}
