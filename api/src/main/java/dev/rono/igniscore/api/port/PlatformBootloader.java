package dev.rono.igniscore.api.port;

/**
 * Version-specific entry point discovered by the bootstrap module at runtime.
 *
 * <p>Implementations are registered via {@link java.util.ServiceLoader}. The
 * bootstrap selects the highest-priority bootloader whose
 * {@link #canBoot(Object)} accepts the host and whose Minecraft version range
 * matches the running server.</p>
 */
public interface PlatformBootloader {

    /**
     * Unique bootloader identifier.
     *
     * @return id such as {@code spigot-v1.21.x} or {@code sponge-v12.0.0}
     */
    String id();

    /**
     * @return host platform family this bootloader targets
     */
    PlatformType platformType();

    /**
     * Minecraft version range this bootloader supports.
     *
     * @return range descriptor, for example {@code 1.21.x}
     */
    String minecraftVersionRange();

    /**
     * Relative boot order when multiple bootloaders match the same host.
     *
     * <p>Higher priority bootloaders are tried first (Paper before Spigot on
     * shared hosts).</p>
     *
     * @return priority value; default is {@code 0}
     */
    default int priority() {
        return 0;
    }

    /**
     * Whether this bootloader can adapt the given host instance.
     *
     * @param host opaque host handle (JavaPlugin, Sponge container, Fabric mod container, etc.)
     * @return {@code true} if {@link #boot(Object)} can produce an adapter for this host
     */
    boolean canBoot(Object host);

    /**
     * Creates and returns the platform adapter for this host.
     *
     * @param host opaque host handle accepted by {@link #canBoot(Object)}
     * @return fully initialized {@link PlatformAdapter}
     */
    PlatformAdapter boot(Object host);
}
