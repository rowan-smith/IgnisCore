package dev.rono.igniscore.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigValueReaderTest {

    @Test
    void getMapListReturnsNestedMaps() {
        Map<String, Object> source = Map.of(
                "entries", List.of(
                        Map.of("id", "a"),
                        Map.of("id", "b")));

        List<Map<String, Object>> maps = ConfigValueReader.getMapList(source, "entries");

        assertEquals(2, maps.size());
        assertEquals("a", maps.get(0).get("id"));
        assertEquals("b", maps.get(1).get("id"));
    }

    @Test
    void getMapListSkipsNonMapEntries() {
        List<Map<String, Object>> maps = ConfigValueReader.asMapList(List.of("bad", Map.of("id", "ok")));

        assertEquals(1, maps.size());
        assertEquals("ok", maps.get(0).get("id"));
    }

    @Test
    void getMapListReturnsEmptyForMissingOrWrongType() {
        assertTrue(ConfigValueReader.getMapList(Map.of("entries", "wrong"), "entries").isEmpty());
        assertTrue(ConfigValueReader.getMapList(null, "entries").isEmpty());
        assertTrue(ConfigValueReader.asMapList(null).isEmpty());
    }
}
