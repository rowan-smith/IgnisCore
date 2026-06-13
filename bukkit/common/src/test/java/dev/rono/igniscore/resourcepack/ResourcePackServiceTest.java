package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePackServiceTest extends MockBukkitTestBase {
    private StubBlockManager blockManager;
    private StubItemManager itemManager;
    private RecordingPackBuilder packBuilder;
    private ResourcePackService resourcePackService;

    @BeforeEach
    void setUpService() throws Exception {
        blockManager = new StubBlockManager(sampleBlock("test-block", 10001));
        itemManager = new StubItemManager();
        packBuilder = new RecordingPackBuilder();
        resourcePackService = new ResourcePackService(
                new IgnisPluginContext(plugin),
                blockManager,
                itemManager,
                new BlockExtensionLoader(null, null),
                new ItemExtensionLoader(null, null),
                packBuilder,
                platformHooks,
                CommonTestSupport.runtimeHost(plugin.getDataFolder().toPath()));
    }

    @Test
    void skipsRebuildWhenFingerprintUnchanged() {
        AtomicBoolean success = new AtomicBoolean();
        resourcePackService.buildAndRegisterAsync(() -> success.set(true), error -> {
            throw new AssertionError(error);
        });
        runAsyncTasks();
        runSyncTasks();
        assertTrue(success.get());
        assertEquals(1, packBuilder.buildCount);

        success.set(false);
        resourcePackService.buildAndRegisterAsync(() -> success.set(true), error -> {
            throw new AssertionError(error);
        });
        runSyncTasks();

        assertTrue(success.get());
        assertEquals(1, packBuilder.buildCount);
    }

    @Test
    void coalescesConcurrentBuildRequests() {
        AtomicInteger successCount = new AtomicInteger();
        Runnable onSuccess = successCount::incrementAndGet;

        resourcePackService.buildAndRegisterAsync(onSuccess, error -> {
            throw new AssertionError(error);
        });
        resourcePackService.buildAndRegisterAsync(onSuccess, error -> {
            throw new AssertionError(error);
        });

        runAsyncTasks();
        runSyncTasks();
        runAsyncTasks();
        runSyncTasks();

        assertEquals(1, packBuilder.buildCount);
        assertEquals(2, successCount.get());
    }

    private void runAsyncTasks() {
        ((BukkitSchedulerMock) server.getScheduler()).waitAsyncTasksFinished();
    }

    private void runSyncTasks() {
        ((BukkitSchedulerMock) server.getScheduler()).performTicks(1);
    }

    private static BlockDefinition sampleBlock(String id, int modelData) {
        return new BlockDefinition(
                id,
                "paper",
                "carrot_on_a_stick",
                Component.text(id),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                modelData,
                false,
                false,
                false,
                id);
    }

    private static final class StubBlockManager extends BlockManager {
        private Map<String, BlockDefinition> definitions;

        StubBlockManager(BlockDefinition definition) {
            super(null, null, null, null, null, null, null);
            this.definitions = Map.of(definition.getId(), definition);
        }

        @Override
        public Map<String, BlockDefinition> getBlockTypes() {
            return definitions;
        }
    }

    private static final class StubItemManager extends ItemManager {
    }

    private static final class RecordingPackBuilder extends ResourcePackBuilder {
        int buildCount;

        RecordingPackBuilder() {
            super(null, null, null);
        }

        @Override
        public PackResult buildPack(Map<String, BlockDefinition> blockDefinitions,
                                    Map<String, ItemDefinition> itemDefinitions) {
            buildCount++;
            File file = new File("target/test-pack-" + buildCount + ".zip");
            return new PackResult(file, "hash" + buildCount);
        }
    }
}
