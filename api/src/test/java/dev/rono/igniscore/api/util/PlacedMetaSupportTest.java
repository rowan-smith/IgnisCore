package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlacedMetaSupportTest {
    @Test
    void clearRemovesPlacementYawAndStringMeta() {
        IgnisLocation location = new IgnisLocation("world", 1, 64, 2);
        PlacedMetaSupport.recordPlacementYaw(location, 90f);
        PlacedMetaSupport.setString(location, "label");

        PlacedMetaSupport.clear(location);

        assertEquals(0f, PlacedMetaSupport.placementYaw(location, 0f));
        assertNull(PlacedMetaSupport.getString(location));
    }

    @Test
    void clearRemovesBothTripwirePartnerEntries() {
        IgnisLocation first = new IgnisLocation("world", 0, 64, 0);
        IgnisLocation second = new IgnisLocation("world", 1, 64, 0);
        PlacedMetaSupport.linkTripwire(first, second);

        PlacedMetaSupport.clear(first);

        assertNull(PlacedMetaSupport.tripwirePartner(second));
        assertNull(PlacedMetaSupport.tripwirePartner(first));
    }
}
