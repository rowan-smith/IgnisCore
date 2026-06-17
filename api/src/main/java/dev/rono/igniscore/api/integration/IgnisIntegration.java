package dev.rono.igniscore.api.integration;

/**
 * Marker for optional third-party integrations exposed to extensions.
 * Each integration reports whether its backing provider is active and which
 * implementation is serving requests (e.g. {@code WorldEdit} vs {@code ignis-world}).
 */
public interface IgnisIntegration {

    /**
     * Stable integration id used in extension manifests ({@code requires-integrations}).
     */
    String integrationId();

    /**
     * Whether this integration can serve requests. Some integrations always return
     * {@code true} when a platform fallback exists (e.g. region editing via {@code IgnisWorld}).
     */
    boolean isEnabled();

    /**
     * Human-readable provider name for logging (e.g. {@code NBT-API}, {@code PDC}, {@code WorldEdit}).
     */
    String providerName();
}
