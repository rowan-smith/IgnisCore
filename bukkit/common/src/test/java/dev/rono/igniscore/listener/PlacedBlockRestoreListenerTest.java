package dev.rono.igniscore.listener;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.world.ChunkLoadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlacedBlockRestoreListenerTest extends MockBukkitTestBase {
    private RecordingBlockManager blockManager;
    private PlacedBlockPersistenceService persistenceService;
    private PlacedBlockRestoreListener listener;

    @BeforeEach
    void setUpListener() {
        blockManager = new RecordingBlockManager();
        persistenceService = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(plugin.getDataFolder().toPath()));
        listener = new PlacedBlockRestoreListener(
                plugin,
                blockManager,
                persistenceService,
                PerformanceSettings.fromValues(1, 32, 3));
    }

    @Test
    void chunkLoadRestoresPlacementsAcrossTicks() {
        Block first = world.getBlockAt(4, 64, 8);
        Block second = world.getBlockAt(5, 64, 8);
        first.setType(Material.BARRIER);
        second.setType(Material.BARRIER);
        persistenceService.recordPlacement(new IgnisLocation("world", 4, 64, 8), "nuke");
        persistenceService.recordPlacement(new IgnisLocation("world", 5, 64, 8), "nuke");
        persistenceService.flush();

        Chunk chunk = world.getChunkAt(0, 0);
        listener.onChunkLoad(new ChunkLoadEvent(chunk, false));
        performTicks(1);

        assertEquals(1, blockManager.restoreCount);
        performTicks(1);
        assertEquals(2, blockManager.restoreCount);
    }

    @Test
    void skipsLocationsWithoutBarrierBlock() {
        persistenceService.recordPlacement(new IgnisLocation("world", 4, 64, 8), "nuke");
        persistenceService.flush();

        Chunk chunk = world.getChunkAt(0, 0);
        listener.onChunkLoad(new ChunkLoadEvent(chunk, false));
        performTicks(2);

        assertEquals(0, blockManager.restoreCount);
        assertNull(blockManager.getPlacedBlockType(new IgnisLocation("world", 4, 64, 8)));
    }

    private void performTicks(long ticks) {
        ((BukkitSchedulerMock) server.getScheduler()).performTicks(ticks);
    }

    private static final class RecordingBlockManager extends BlockManager {
        int restoreCount;

        RecordingBlockManager() {
            super(null, null, null, null, null, null, null, PerformanceSettings.defaults());
        }

        @Override
        public void restorePlacedBlock(IgnisLocation location, String typeId) {
            restoreCount++;
        }

        @Override
        public String getPlacedBlockType(IgnisLocation location) {
            return null;
        }
    }
}
