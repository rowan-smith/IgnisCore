package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.paper.adapter.PaperPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperV121Bootloader implements PlatformBootloader {

    private static final String PAPER_MARKER = "io.papermc.paper.datacomponent.DataComponentTypes";

    @Override
    public String id() {
        return "paper-v1.21.x";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.PAPER;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.21.x";
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean canBoot(Object host) {
        if (!(host instanceof JavaPlugin)) {
            return false;
        }
        try {
            Class.forName(PAPER_MARKER);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @Override
    public PlatformAdapter boot(Object host) {
        return new PaperPlatformAdapter((JavaPlugin) host);
    }
}
