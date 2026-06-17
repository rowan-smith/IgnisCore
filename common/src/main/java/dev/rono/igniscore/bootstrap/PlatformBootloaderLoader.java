package dev.rono.igniscore.bootstrap;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;

import java.util.Comparator;
import java.util.ServiceLoader;

/**
 * Discovers and selects the highest-priority {@link PlatformBootloader} for the current host.
 */
public final class PlatformBootloaderLoader {
    private PlatformBootloaderLoader() {
    }

    public static PlatformAdapter boot(Object host) {
        return resolve(host).boot(host);
    }

    public static PlatformBootloader resolve(Object host) {
        return select(host);
    }

    private static PlatformBootloader select(Object host) {
        return ServiceLoader.load(PlatformBootloader.class, classLoaderFor(host)).stream()
                .map(ServiceLoader.Provider::get)
                .filter(bootloader -> bootloader.canBoot(host))
                .max(Comparator.comparingInt(PlatformBootloader::priority)
                        .thenComparing(PlatformBootloader::id))
                .orElseThrow(() -> new IllegalStateException(
                        "No PlatformBootloader found for host " + host.getClass().getName()));
    }

    private static ClassLoader classLoaderFor(Object host) {
        if (host != null) {
            ClassLoader hostLoader = host.getClass().getClassLoader();
            if (hostLoader != null) {
                return hostLoader;
            }
        }
        ClassLoader interfaceLoader = PlatformBootloader.class.getClassLoader();
        return interfaceLoader != null ? interfaceLoader : ClassLoader.getSystemClassLoader();
    }
}
