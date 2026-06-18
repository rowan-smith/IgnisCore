package dev.rono.igniscore.api.model;

/**
 * Parsed {@code textures.fallback} value from extension {@code config.yml}.
 *
 * <p>{@code minecraft:tnt} resolves to vanilla assets; bare {@code grenade} resolves to another
 * extension or the bundled igniscore fallback catalog; {@code igniscore:mine} targets the catalog
 * explicitly.</p>
 */
public record TextureFallbackReference(String namespace, String id) {
    public static final String NAMESPACE_MINECRAFT = "minecraft";
    public static final String NAMESPACE_IGNISCORE = "igniscore";

    /**
     * Parses a raw fallback string from YAML.
     *
     * @param raw value from {@code textures.fallback}, or {@code null} when omitted
     * @return parsed reference, or {@code null} when {@code raw} is blank
     */
    public static TextureFallbackReference parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        int colon = raw.indexOf(':');
        if (colon >= 0) {
            return new TextureFallbackReference(raw.substring(0, colon), raw.substring(colon + 1));
        }
        return new TextureFallbackReference(NAMESPACE_IGNISCORE, raw);
    }

    /**
     * @return {@code true} when this reference targets vanilla Minecraft assets
     */
    public boolean isMinecraft() {
        return NAMESPACE_MINECRAFT.equals(namespace);
    }

    /**
     * @return {@code true} when this reference targets the igniscore fallback catalog
     */
    public boolean isIgniscoreCatalog() {
        return NAMESPACE_IGNISCORE.equals(namespace);
    }

    /**
     * @return canonical {@code namespace:id} form
     */
    public String canonical() {
        return namespace + ":" + id;
    }
}
