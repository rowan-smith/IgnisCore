package dev.rono.igniscore.sponge.v850.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.common.version.MinecraftVersions;
import dev.rono.igniscore.sponge.v850.IgnisSpongePlugin;
import dev.rono.igniscore.sponge.v850.adapter.SpongePlatformAdapter;

public final class SpongeV850Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "sponge-v8.5.0";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.SPONGE;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.20.x";
    }

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public boolean canBoot(Object host) {
        if (!(host instanceof IgnisSpongePlugin plugin)) {
            return false;
        }
        return matchesMinecraftLine(plugin.game().platform().minecraftVersion().name(), 1, 20);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        if (!(host instanceof IgnisSpongePlugin plugin)) {
            throw new IllegalArgumentException("Sponge v8.5 bootloader requires IgnisSpongePlugin host");
        }
        return new SpongePlatformAdapter(plugin, plugin.container(), plugin.game(), plugin.game().eventManager());
    }

    private static boolean matchesMinecraftLine(String version, int major, int minor) {
        return MinecraftVersions.matchesMinorLine(version, major, minor);
    }
}
