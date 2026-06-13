package dev.rono.igniscore.paper.adapter;

import dev.rono.igniscore.platform.paper.PaperPlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatformAdapter extends BukkitPlatformAdapter {

    public PaperPlatformAdapter(JavaPlugin plugin) {
        super(plugin, new PaperPlatformHooks());
    }
}
