package dev.rono.igniscore.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigValueReaderTest {
    @Test
    void getMapReturnsNestedMap() {
        Map<String, Object> nested = Map.of("value", 1);
        Map<String, Object> source = Map.of("nested", nested);

        assertSame(nested, ConfigValueReader.getMap(source, "nested"));
    }

    @Test
    void getMapReturnsEmptyMapForMissingOrWrongType() {
        assertTrue(ConfigValueReader.getMap(Map.of("nested", "wrong"), "nested").isEmpty());
        assertTrue(ConfigValueReader.getMap(null, "nested").isEmpty());
    }

    @Test
    void getListReturnsConfiguredList() {
        List<String> values = List.of("a", "b");

        assertSame(values, ConfigValueReader.getList(Map.of("values", values), "values"));
    }

    @Test
    void getStringFallsBackWhenMissing() {
        assertEquals("fallback", ConfigValueReader.getString(Map.of(), "value", "fallback"));
        assertEquals("123", ConfigValueReader.getString(Map.of("value", 123), "value", "fallback"));
    }

    @Test
    void getIntParsesNumbersAndStrings() {
        assertEquals(7, ConfigValueReader.getInt(Map.of("value", 7), "value", 1));
        assertEquals(8, ConfigValueReader.getInt(Map.of("value", "8"), "value", 1));
        assertEquals(1, ConfigValueReader.getInt(Map.of("value", "bad"), "value", 1));
    }

    @Test
    void getDoubleParsesNumbersAndStrings() {
        assertEquals(7.5, ConfigValueReader.getDouble(Map.of("value", 7.5), "value", 1.0));
        assertEquals(8.5, ConfigValueReader.getDouble(Map.of("value", "8.5"), "value", 1.0));
        assertEquals(1.0, ConfigValueReader.getDouble(Map.of("value", "bad"), "value", 1.0));
    }
}
