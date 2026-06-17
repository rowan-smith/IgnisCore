package dev.rono.igniscore.api.extension;

import java.util.Locale;

/**
 * Optional platform integrations declared in extension manifests via
 * {@code requires-integrations}.
 *
 * <p>Extensions that list an integration may degrade or skip behavior when the
 * integration is unavailable on the current platform (for example Sponge noop protocol).</p>
 *
 * @see ExtensionManifest#getRequiredIntegrations()
 * @see ExtensionRequirements#validate(ExtensionManifest, ExtensionRuntimeCapabilities)
 */
public enum ExtensionIntegration {
    /**
     * Client protocol hooks (ProtocolLib on Bukkit). Used for fake explosions,
     * camera attachment, and other packet-level effects.
     */
    PROTOCOL("protocol"),

    /**
     * Persistent item and entity NBT access. Required for tools that stamp data
     * onto items or read entity metadata across sessions.
     */
    NBT_ENTITY("nbt-entity");

    private final String manifestKey;

    ExtensionIntegration(String manifestKey) {
        this.manifestKey = manifestKey;
    }

    /**
     * @return YAML manifest token for this integration
     */
    public String manifestKey() {
        return manifestKey;
    }

    /**
     * Parses a manifest entry (case-insensitive, hyphen or underscore).
     *
     * @throws IllegalArgumentException if the token is unknown
     */
    public static ExtensionIntegration fromManifest(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Integration token must not be blank");
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        for (ExtensionIntegration integration : values()) {
            if (integration.manifestKey.equals(normalized)) {
                return integration;
            }
        }
        throw new IllegalArgumentException("Unknown requires-integrations entry: " + raw);
    }
}
