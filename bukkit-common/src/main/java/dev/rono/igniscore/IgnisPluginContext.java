package dev.rono.igniscore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Shared Bukkit plugin helpers used by platform plugin entrypoints.
 */
public final class IgnisPluginContext {
    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private volatile boolean debugEnabled;

    public IgnisPluginContext(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin plugin() {
        return plugin;
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
