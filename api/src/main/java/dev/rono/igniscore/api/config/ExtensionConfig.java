package dev.rono.igniscore.api.config;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Typed read-only view over extension YAML maps such as {@code custom_data} and {@code interactions}.
 *
 * <p>Use {@link dev.rono.igniscore.api.model.BlockDefinition#getCustomConfig()} or
 * {@link dev.rono.igniscore.api.model.ItemDefinition#getCustomConfig()} from strategies to
 * access nested sections without casting raw maps.</p>
 */
public final class ExtensionConfig {
    private static final ExtensionConfig EMPTY = new ExtensionConfig(Map.of());

    private final Map<String, Object> values;

    private ExtensionConfig(Map<String, Object> values) {
        this.values = values == null || values.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(values);
    }

    /**
     * Wraps a YAML section map as a typed config view.
     *
     * @param values section map; {@code null} or empty yields the shared empty instance
     * @return read-only config view
     */
    public static ExtensionConfig of(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return EMPTY;
        }
        return new ExtensionConfig(values);
    }

    /**
     * @return shared empty config with no keys
     */
    public static ExtensionConfig empty() {
        return EMPTY;
    }

    /**
     * @return unmodifiable backing map
     */
    public Map<String, Object> asMap() {
        return values;
    }

    /**
     * @param key YAML key
     * @return {@code true} when the key is present
     */
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    /**
     * @param key YAML key
     * @return optional raw value without type coercion
     */
    public Optional<Object> raw(String key) {
        return Optional.ofNullable(values.get(key));
    }

    /**
     * @param key YAML key
     * @param defaultValue value when the key is absent
     * @return string form of the value, or the default
     */
    public String getString(String key, String defaultValue) {
        Object value = values.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * @param key YAML key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed integer
     */
    public int getInt(String key, int defaultValue) {
        return asNumber(key).map(Number::intValue).orElse(defaultValue);
    }

    /**
     * @param key YAML key
     * @param defaultValue value when the key is absent or not numeric
     * @return parsed double
     */
    public double getDouble(String key, double defaultValue) {
        return asNumber(key).map(Number::doubleValue).orElse(defaultValue);
    }

    /**
     * @param key YAML key
     * @param defaultValue value when the key is absent or not a boolean
     * @return parsed boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = values.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return defaultValue;
    }

    /**
     * Returns a nested map as a child config view.
     *
     * @param key section key
     * @return child config, or {@link #empty()} when the key is missing or not a map
     */
    public ExtensionConfig section(String key) {
        Object value = values.get(key);
        if (value instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> section = (Map<String, Object>) map;
            return of(section);
        }
        return empty();
    }

    private Optional<Number> asNumber(String key) {
        Object value = values.get(key);
        if (value instanceof Number number) {
            return Optional.of(number);
        }
        if (value != null) {
            try {
                return Optional.of(Double.parseDouble(value.toString()));
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ExtensionConfig config && Objects.equals(values, config.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
