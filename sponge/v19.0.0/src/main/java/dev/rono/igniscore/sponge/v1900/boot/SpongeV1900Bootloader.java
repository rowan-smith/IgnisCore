package dev.rono.igniscore.sponge.v1900.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.bootstrap.SpongeBootloaderSupport;
import dev.rono.igniscore.sponge.v1900.IgnisSpongePlugin;
import dev.rono.igniscore.sponge.v1900.adapter.SpongePlatformAdapter;

public final class SpongeV1900Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "sponge-v19.0.0";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.SPONGE;
    }

    @Override
    public String minecraftVersionRange() {
        return "26.1.x";
    }

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public boolean canBoot(Object host) {
        return SpongeBootloaderSupport.acceptsHost(host, IgnisSpongePlugin.class,
                plugin -> plugin.game().platform().minecraftVersion().name(), 26, 1);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        IgnisSpongePlugin plugin = SpongeBootloaderSupport.requireHost(host, IgnisSpongePlugin.class, id());
        return new SpongePlatformAdapter(plugin, plugin.container(), plugin.game(), plugin.game().eventManager());
    }
}
