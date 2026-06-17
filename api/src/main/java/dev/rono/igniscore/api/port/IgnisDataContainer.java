package dev.rono.igniscore.api.port;

/**
 * Platform-neutral structured data store for runtime block state.
 *
 * <p>Keys are plain strings scoped to a single block instance. Missing keys
 * return type-specific defaults (false, 0, 0.0, or {@code null} for strings).</p>
 */
public interface IgnisDataContainer {

    /**
     * Stores a boolean value under the given key.
     *
     * @param key storage key
     * @param value value to store
     */
    void setBoolean(String key, boolean value);

    /**
     * @param key storage key
     * @return stored boolean, or {@code false} when absent
     */
    boolean getBoolean(String key);

    /**
     * Stores an integer value under the given key.
     *
     * @param key storage key
     * @param value value to store
     */
    void setInt(String key, int value);

    /**
     * @param key storage key
     * @return stored integer, or {@code 0} when absent
     */
    int getInt(String key);

    /**
     * Stores a double value under the given key.
     *
     * @param key storage key
     * @param value value to store
     */
    void setDouble(String key, double value);

    /**
     * @param key storage key
     * @return stored double, or {@code 0.0} when absent
     */
    double getDouble(String key);

    /**
     * Stores a string value under the given key.
     *
     * @param key storage key
     * @param value value to store
     */
    void setString(String key, String value);

    /**
     * @param key storage key
     * @return stored string, or {@code null} when absent
     */
    String getString(String key);

    /**
     * @param key storage key
     * @return whether a value has been stored for this key
     */
    boolean hasKey(String key);
}
