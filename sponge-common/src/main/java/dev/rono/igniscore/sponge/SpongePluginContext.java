package dev.rono.igniscore.sponge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.logging.log4j.Logger;

public final class SpongePluginContext {
    private final SpongePluginHost plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private volatile boolean debugEnabled;

    public SpongePluginContext(SpongePluginHost plugin) {
        this.plugin = plugin;
    }

    public SpongePluginHost plugin() {
        return plugin;
    }

    public Logger logger() {
        return plugin.getLogger();
    }

    public Component message(String raw) {
        return miniMessage.deserialize(raw);
    }

    public void debug(String message) {
        if (debugEnabled) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
}
