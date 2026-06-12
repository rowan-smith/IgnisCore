package dev.rono.igniscore.api.port;

/**
 * Version-specific entry point discovered by the bootstrap module at runtime.
 * Implementations are registered via {@link java.util.ServiceLoader}.
 */
public interface PlatformBootloader {

    /**
     * Unique id, e.g. {@code spigot-v1.21.x} or {@code sponge-v12.0.0}.
     */
    String id();

    PlatformType platformType();

    /**
     * Minecraft version range this bootloader supports, e.g. {@code 1.21.x}.
     */
    String minecraftVersionRange();

    /**
     * Higher priority bootloaders are tried first (Paper before Spigot on shared hosts).
     */
    default int priority() {
        return 0;
    }

    /**
     * @param host opaque host handle (JavaPlugin, Sponge Container, Fabric mod container, etc.)
     */
    boolean canBoot(Object host);

    /**
     * Creates and returns the platform adapter for this host.
     */
    PlatformAdapter boot(Object host);
}
