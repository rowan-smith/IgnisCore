package dev.rono.igniscore.api.config;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Typed read-only view over extension YAML maps such as {@code custom_data} and {@code interactions}.
 * Use {@link dev.rono.igniscore.api.model.BlockDefinition#getCustomConfig()} or
 * {@link dev.rono.igniscore.api.model.ItemDefinition#getCustomConfig()} from strategies.
 */
public final class ExtensionConfig {
    private static final ExtensionConfig EMPTY = new ExtensionConfig(Map.of());

    private final Map<String, Object> values;

    private ExtensionConfig(Map<String, Object> values) {
        this.values = values == null || values.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(values);
    }

    public static ExtensionConfig of(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return EMPTY;
        }
        return new ExtensionConfig(values);
    }

    public static ExtensionConfig empty() {
        return EMPTY;
    }

    public Map<String, Object> asMap() {
        return values;
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public Optional<Object> raw(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public String getString(String key, String defaultValue) {
        Object value = values.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        return asNumber(key).map(Number::intValue).orElse(defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        return asNumber(key).map(Number::doubleValue).orElse(defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = values.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return defaultValue;
    }

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
