package dev.rono.igniscore.api.strategy;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategySupportTest {
    @Test
    void readsCustomValuesFromMap() {
        Map<String, Object> customData = Map.of(
                "power", 7.0,
                "fire", true,
                "blockDamage", false
        );

        assertEquals(7.0, StrategySupport.customDouble(customData, "power", 1.0));
        assertEquals(1.0, StrategySupport.customDouble(customData, "missing", 1.0));
        assertTrue(StrategySupport.customBoolean(customData, "fire", false));
        assertFalse(StrategySupport.customBoolean(customData, "blockDamage", true));
        assertTrue(StrategySupport.customBoolean(customData, "missing", true));
        assertEquals(12, StrategySupport.customInt(customData, "missing", 12));
    }
}
