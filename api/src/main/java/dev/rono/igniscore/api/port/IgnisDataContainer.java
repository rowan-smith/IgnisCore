package dev.rono.igniscore.api.port;

/**
 * Platform-neutral structured data store for runtime block state.
 */
public interface IgnisDataContainer {

    void setBoolean(String key, boolean value);

    boolean getBoolean(String key);

    void setInt(String key, int value);

    int getInt(String key);

    void setDouble(String key, double value);

    double getDouble(String key);

    void setString(String key, String value);

    String getString(String key);

    boolean hasKey(String key);
}
