package dev.rono.igniscore.api.port;

/**
 * Identifies a supported host platform and Minecraft version line.
 *
 * <p>Reported by {@link PlatformBootloader#platformType()} and
 * {@link PlatformAdapter#getPlatformType()} so shared code can branch on
 * capabilities when necessary.</p>
 */
public enum PlatformType {

    /** CraftBukkit / Spigot server API. */
    SPIGOT,

    /** Paper server (Spigot fork with extended API). */
    PAPER,

    /** SpongeAPI-powered server. */
    SPONGE,

    /** Fabric mod loader with Minecraft server. */
    FABRIC
}
