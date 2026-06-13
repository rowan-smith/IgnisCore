package dev.rono.igniscore.paper.adapter;

import dev.rono.igniscore.platform.paper.PaperPlatformHooks;
import dev.rono.igniscore.spigot.adapter.SpigotPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatformAdapter extends SpigotPlatformAdapter {

    public PaperPlatformAdapter(JavaPlugin plugin) {
        super(plugin, new PaperPlatformHooks());
    }
}
