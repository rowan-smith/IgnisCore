package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Platform-neutral item and entity persistent data access.
 *
 * <p>Keys should be namespaced (for example {@code ignis:coin_flip}) to avoid
 * collisions with other plugins. When {@link #isEnabled()} returns false, mutators
 * are no-ops and readers return defaults.</p>
 *
 * @see dev.rono.igniscore.api.extension.ExtensionIntegration#NBT_ENTITY
 */
public interface IgnisNbtService extends IgnisIntegration {

    @Override
    default String integrationId() {
        return IgnisIntegrations.NBT;
    }

    /**
     * Whether entity-scoped data is supported (requires NBT-API on Bukkit).
     */
    default boolean supportsEntityData() {
        return false;
    }

    /**
     * @return whether NBT read/write is available on this platform
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Stores a string on an item stack.
     *
     * @param item target item handle
     * @param key namespaced key
     * @param value string value
     */
    void setItemString(IgnisItem item, String key, String value);

    /**
     * @param item target item handle
     * @param key namespaced key
     * @return stored string, or empty when absent
     */
    String getItemString(IgnisItem item, String key);

    /**
     * Stores an integer on an item stack.
     */
    void setItemInt(IgnisItem item, String key, int value);

    /**
     * @param defaultValue returned when the key is absent
     */
    int getItemInt(IgnisItem item, String key, int defaultValue);

    /**
     * Stores a boolean on an item stack.
     */
    void setItemBoolean(IgnisItem item, String key, boolean value);

    /**
     * @param defaultValue returned when the key is absent
     */
    boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue);

    /**
     * Stores a string on a platform entity handle.
     *
     * @param nativeEntity opaque entity (for example Bukkit {@code Entity})
     */
    void setEntityString(Object nativeEntity, String key, String value);

    /**
     * @param nativeEntity opaque entity handle
     * @return stored string, or empty when absent
     */
    String getEntityString(Object nativeEntity, String key);
}
