package dev.rono.igniscore.manager;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.StrategyProfileResolver;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockManagerBehaviorTest {
    @TempDir
    Path tempDir;

    private BehaviorTestSupport.TestContext ctx;
    private CommonTestSupport.RecordingBlockVisualRenderer visualRenderer;
    private PlacedBlockPersistenceService persistence;
    private BlockManager blockManager;
    private BlockDefinition definition;
    private IgnisEventBusImpl eventBus;

    @BeforeEach
    void setUp() throws Exception {
        eventBus = new IgnisEventBusImpl();
        ctx = BehaviorTestSupport.createContext(eventBus);
        visualRenderer = new CommonTestSupport.RecordingBlockVisualRenderer();
        persistence = new PlacedBlockPersistenceService(CommonTestSupport.runtimeHost(tempDir));
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl(
                new DefaultExplosionStrategy(ctx.context().getExtensionSupport(), eventBus));
        StrategyProfileResolver profileResolver = new StrategyProfileResolver(registry);
        StrategyEventPublisher events = new StrategyEventPublisher(eventBus, profileResolver);
        blockManager = new BlockManager(
                new RuntimeBlockService(),
                ctx.effects(),
                profileResolver,
                persistence,
                new CommonTestSupport.ImmediateIgnisScheduler(),
                visualRenderer,
                events,
                PerformanceSettings.fromValues(1, 1, 3));
        definition = sampleDefinition();
        DefaultExplosionStrategy testStrategy = new DefaultExplosionStrategy(ctx.context().getExtensionSupport(), eventBus);
        testStrategy.bindDescriptor(IgnisStrategyDescriptor.of(definition.getExtensionId(), "Test Block", "1.0.0", "test"));
        testStrategy.registerEvents();
        registry.register(
                IgnisStrategyDescriptor.of(definition.getExtensionId(), "Test Block", "1.0.0", "test"),
                testStrategy);
        blockManager.loadFromExtensions(List.of(CommonTestSupport.loadedBlock(definition)));
    }

    @AfterEach
    void tearDown() {
        if (persistence != null) {
            persistence.shutdown();
        }
    }

    @Test
    void registerPlacedBlockSpawnsStaticDisplayAndPersists() {
        IgnisLocation location = new IgnisLocation("world", 4, 64, 8);

        blockManager.registerPlacedBlock(location, definition.getId());

        assertEquals(definition.getId(), blockManager.getPlacedBlockType(location));
        assertEquals(1, visualRenderer.staticDisplays().size());
        assertEquals(definition.getId(), persistence.entriesInChunk("world", 0, 0).get("4,64,8"));
    }

    @Test
    void unregisterPlacedBlockRemovesDisplayAndPersistence() {
        IgnisLocation location = new IgnisLocation("world", 4, 64, 8);
        blockManager.registerPlacedBlock(location, definition.getId());

        blockManager.unregisterPlacedBlock(location);

        assertNull(blockManager.getPlacedBlockType(location));
        assertEquals(1, visualRenderer.removedStaticCount());
        assertTrue(persistence.entriesInChunk("world", 0, 0).isEmpty());
    }

    @Test
    void executeBehaviorTriggersDefaultExplosionStrategy() {
        RuntimeBlockInstance instance = new RuntimeBlockService().createInstance(
                definition, new IgnisLocation("world", 1, 64, 1));

        blockManager.executeBehavior(instance, null);

        assertFalse(ctx.world().explosions().isEmpty());
        assertEquals(1, visualRenderer.removedAnimatedCount());
    }

    @Test
    void triggerBlockCreatesAnimatedInstance() {
        IgnisLocation location = new IgnisLocation("world", 2, 64, 2);

        RuntimeBlockInstance instance = blockManager.triggerBlock(location, definition.getId(), null);

        assertNotNull(instance);
        assertEquals(1, visualRenderer.animatedDisplayCount());
    }

    @Test
    void stopActiveBlocksCancelsRuntimeInstances() {
        IgnisLocation location = new IgnisLocation("world", 2, 64, 2);
        blockManager.triggerBlock(location, definition.getId(), null);

        blockManager.stopActiveBlocks();

        assertTrue(blockManager.getActiveBlocks().isEmpty());
        assertTrue(visualRenderer.removedAnimatedCount() >= 1);
    }

    @Test
    void refreshPlacedBlockVisualsBatchesAcrossSchedulerTicks() throws Exception {
        CommonTestSupport.DeferredIgnisScheduler deferredScheduler = new CommonTestSupport.DeferredIgnisScheduler();
        CommonTestSupport.RecordingBlockVisualRenderer batchedRenderer = new CommonTestSupport.RecordingBlockVisualRenderer();
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl(
                new DefaultExplosionStrategy(ctx.context().getExtensionSupport(), eventBus));
        StrategyProfileResolver profileResolver = new StrategyProfileResolver(registry);
        StrategyEventPublisher events = new StrategyEventPublisher(eventBus, profileResolver);
        BlockManager batchedManager = new BlockManager(
                new RuntimeBlockService(),
                ctx.effects(),
                profileResolver,
                persistence,
                deferredScheduler,
                batchedRenderer,
                events,
                PerformanceSettings.fromValues(16, 1, 3));
        batchedManager.loadFromExtensions(List.of(CommonTestSupport.loadedBlock(definition)));
        batchedManager.registerPlacedBlock(new IgnisLocation("world", 4, 64, 8), definition.getId());
        batchedManager.registerPlacedBlock(new IgnisLocation("world", 5, 64, 8), definition.getId());

        batchedManager.refreshPlacedBlockVisuals();

        assertEquals(1, batchedRenderer.removedStaticCount());
        deferredScheduler.runPending();
        assertEquals(2, batchedRenderer.removedStaticCount());
    }

    @Test
    void refreshPlacedBlockVisualsRespawnsDisplays() {
        IgnisLocation location = new IgnisLocation("world", 4, 64, 8);
        blockManager.registerPlacedBlock(location, definition.getId());
        int beforeRefresh = visualRenderer.removedStaticCount();

        blockManager.refreshPlacedBlockVisuals();

        assertEquals(beforeRefresh + 1, visualRenderer.removedStaticCount());
        assertEquals(2, visualRenderer.staticDisplays().size());
    }

    @Test
    void cleanupClearsActiveDisplays() {
        IgnisLocation location = new IgnisLocation("world", 2, 64, 2);
        blockManager.registerPlacedBlock(location, definition.getId());
        blockManager.triggerBlock(location, definition.getId(), null);

        blockManager.cleanup();

        assertNull(blockManager.getPlacedBlockType(location));
        assertTrue(visualRenderer.removedStaticCount() >= 1);
        assertTrue(visualRenderer.removedAnimatedCount() >= 1);
    }

    private static BlockDefinition sampleDefinition() {
        return new BlockDefinition(
                "test-block",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                java.util.Map.of("fuse", 80, "radius", 4.0),
                java.util.Map.of(),
                java.util.Map.of(),
                java.util.Map.of(),
                10001,
                false,
                false,
                false,
                "test-block");
    }
}
