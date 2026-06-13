package dev.rono.igniscore.sponge.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.sponge.IgnisSpongePlugin;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import org.spongepowered.plugin.builtin.jvm.Plugin;

public final class SpongeV1200Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "sponge-v12.0.0";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.SPONGE;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.21.x";
    }

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public boolean canBoot(Object host) {
        if (host instanceof IgnisSpongePlugin) {
            return true;
        }
        return hasSpongePluginMarker(host);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        if (!(host instanceof IgnisSpongePlugin plugin)) {
            throw new IllegalArgumentException("Sponge bootloader requires IgnisSpongePlugin host");
        }
        return new SpongePlatformAdapter(plugin, plugin.container(), plugin.game(), plugin.game().eventManager());
    }

    private static boolean hasSpongePluginMarker(Object host) {
        if (host == null) {
            return false;
        }
        ClassLoader classLoader = host.getClass().getClassLoader();
        if (classLoader != null && classLoader.getResource("META-INF/sponge_plugins.json") != null) {
            return true;
        }
        return host.getClass().isAnnotationPresent(Plugin.class);
    }
}
