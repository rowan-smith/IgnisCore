package dev.rono.igniscore.api.port;

import java.util.HashMap;
import java.util.Map;

/**
 * Default in-memory {@link IgnisDataContainer} used by {@link dev.rono.igniscore.api.model.RuntimeBlockInstance}.
 *
 * <p>Stores primitive and string values in a hash map. Not persisted across
 * restarts unless copied elsewhere by the runtime.</p>
 */
public final class MapIgnisDataContainer implements IgnisDataContainer {
    private final Map<String, Object> values = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public void setBoolean(String key, boolean value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getBoolean(String key) {
        Object value = values.get(key);
        return value instanceof Boolean bool && bool;
    }

    /** {@inheritDoc} */
    @Override
    public void setInt(String key, int value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public int getInt(String key) {
        Object value = values.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setDouble(String key, double value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public double getDouble(String key) {
        Object value = values.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return 0.0;
    }

    /** {@inheritDoc} */
    @Override
    public void setString(String key, String value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public String getString(String key) {
        Object value = values.get(key);
        return value != null ? value.toString() : null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasKey(String key) {
        return values.containsKey(key);
    }
}
