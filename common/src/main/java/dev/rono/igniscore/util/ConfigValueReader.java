package dev.rono.igniscore.util;

import java.util.List;
import java.util.Map;

public final class ConfigValueReader {
    private ConfigValueReader() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Map<String, Object> source, String key) {
        if (source == null) {
            return Map.of();
        }
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    public static List<?> getList(Map<String, Object> source, String key) {
        if (source == null) {
            return List.of();
        }
        Object value = source.get(key);
        if (value instanceof List<?> list) {
            return list;
        }
        return List.of();
    }

    public static String getString(Map<String, Object> source, String key, String defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        Object value = source.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public static int getInt(Map<String, Object> source, String key, int defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        return asInt(source.get(key), defaultValue);
    }

    public static int asInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static double getDouble(Map<String, Object> source, String key, double defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
