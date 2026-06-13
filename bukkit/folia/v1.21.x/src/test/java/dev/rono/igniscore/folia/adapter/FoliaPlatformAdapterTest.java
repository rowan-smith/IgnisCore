package dev.rono.igniscore.folia.adapter;

import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.folia.support.FoliaMockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoliaPlatformAdapterTest extends FoliaMockBukkitTestBase {

    @Test
    void reportsFoliaPlatformType() {
        FoliaPlatformAdapter adapter = new FoliaPlatformAdapter(plugin);

        assertEquals(PlatformType.FOLIA, adapter.getPlatformType());
    }
}
