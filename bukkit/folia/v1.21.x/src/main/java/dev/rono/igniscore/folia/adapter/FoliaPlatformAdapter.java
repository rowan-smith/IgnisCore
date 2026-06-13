package dev.rono.igniscore.folia.adapter;

import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.paper.adapter.PaperPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaPlatformAdapter extends PaperPlatformAdapter {

    public FoliaPlatformAdapter(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.FOLIA;
    }
}
