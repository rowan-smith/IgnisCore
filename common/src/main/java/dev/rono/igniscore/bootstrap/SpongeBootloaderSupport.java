package dev.rono.igniscore.bootstrap;

import dev.rono.igniscore.common.version.MinecraftVersions;

import java.util.function.Function;

public final class SpongeBootloaderSupport {
    private SpongeBootloaderSupport() {
    }

    public static <P> boolean acceptsHost(Object host,
                                          Class<P> pluginType,
                                          Function<P, String> version,
                                          int major,
                                          int minor) {
        if (!pluginType.isInstance(host)) {
            return false;
        }
        return MinecraftVersions.matchesMinorLine(version.apply(pluginType.cast(host)), major, minor);
    }

    public static <P> P requireHost(Object host, Class<P> pluginType, String bootloaderId) {
        if (!pluginType.isInstance(host)) {
            throw new IllegalArgumentException("Bootloader " + bootloaderId + " requires "
                    + pluginType.getName() + " host");
        }
        return pluginType.cast(host);
    }
}
