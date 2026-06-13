package dev.rono.igniscore.service;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionSupportServiceTest {
    @TempDir
    Path tempDir;

    private ExtensionSupportService service;
    private BehaviorTestSupport.RecordingIgnisWorld world;

    @BeforeEach
    void setUp() {
        world = new BehaviorTestSupport.RecordingIgnisWorld();
        service = new ExtensionSupportService(CommonTestSupport.platformAdapter(world, tempDir));
    }

    @Test
    void tryCollectRoutesDropsToRegisteredCollector() {
        IgnisLocation cacheLocation = new IgnisLocation("world", 10, 64, 10);
        IgnisLocation breakLocation = new IgnisLocation("world", 10.9, 64.2, 10.1);
        service.registerDropCollector(cacheLocation, (location, drops) -> {
            if (location.x() == 10 && location.y() == 64 && location.z() == 10) {
                drops.clear();
                return true;
            }
            return false;
        });

        List<IgnisItem> drops = new ArrayList<>(List.of(testItem(3), testItem(1)));
        boolean collected = service.tryCollect(breakLocation, drops);

        assertTrue(collected);
        assertTrue(drops.isEmpty());
    }

    @Test
    void tryCollectFiltersZeroAmountStacks() {
        List<IgnisItem> drops = new ArrayList<>(List.of(testItem(0), testItem(2)));

        service.tryCollect(new IgnisLocation("world", 1, 2, 3), drops);

        assertEquals(1, drops.size());
        assertEquals(2, drops.getFirst().getAmount());
    }

    @Test
    void clearRemovesCollectorsAndInventories() {
        service.registerDropCollector(new IgnisLocation("world", 1, 2, 3), (location, drops) -> false);
        Object inventory = new Object();
        service.registerCustomInventory(inventory, new dev.rono.igniscore.api.inventory.IgnisCustomInventory() {
            @Override
            public boolean accepts(dev.rono.igniscore.api.port.IgnisItem stack) {
                return false;
            }

            @Override
            public void restoreDecorations() {
            }

            @Override
            public boolean isSeparatorSlot(int slot) {
                return false;
            }
        });

        service.clear();

        assertFalse(service.tryCollect(new IgnisLocation("world", 1, 2, 3), new ArrayList<>(List.of(testItem(1)))));
        assertEquals(null, service.getCustomInventory(inventory));
    }

    private static IgnisItem testItem(int amount) {
        return new IgnisItem() {
            @Override
            public int getAmount() {
                return amount;
            }

            @Override
            public void setAmount(int amount) {
            }

            @Override
            public String getMaterialKey() {
                return "stone";
            }

            @Override
            public boolean isAir() {
                return amount <= 0;
            }

            @Override
            public Object nativeItem() {
                return this;
            }
        };
    }
}
