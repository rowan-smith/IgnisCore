package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Platform-neutral item and entity data access.
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

    void setItemString(IgnisItem item, String key, String value);

    String getItemString(IgnisItem item, String key);

    void setItemInt(IgnisItem item, String key, int value);

    int getItemInt(IgnisItem item, String key, int defaultValue);

    void setItemBoolean(IgnisItem item, String key, boolean value);

    boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue);

    void setEntityString(Object nativeEntity, String key, String value);

    String getEntityString(Object nativeEntity, String key);
}
