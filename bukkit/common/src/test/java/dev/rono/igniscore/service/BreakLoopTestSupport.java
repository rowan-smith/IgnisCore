package dev.rono.igniscore.service;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ExtensionSupportListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;
import dev.rono.igniscore.support.PdcBackedNbtService;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class BreakLoopTestSupport {
    private BreakLoopTestSupport() {
    }

    public record Context(
            BlockManager blockManager,
            CustomBlockBreakService breakService,
            CustomBlockIgnitionService ignitionService,
            BlockItemFactory blockItemFactory,
            BlockItemIdentifier blockItemIdentifier,
            ItemIdentifier itemIdentifier,
            IgnisStrategyRegistryImpl strategyRegistry,
            ConfiguredEffectService effectService,
            ExtensionSupportService extensionSupport,
            CustomBlockPlacementService placementService,
            BlockListener blockListener,
            ExtensionSupportListener extensionSupportListener,
            BehaviorTestSupport.TestContext behaviorContext) {
    }

    public static Context create(JavaPlugin plugin,
                                 ServerMock server,
                                 dev.rono.igniscore.platform.PlatformHooks platformHooks,
                                 Path tempDir,
                                 BlockDefinition... definitions) throws Exception {
        BehaviorTestSupport.TestContext behaviorContext = BehaviorTestSupport.createContext();
        ExtensionSupportService extensionSupport = new ExtensionSupportService(
                CommonTestSupport.platformAdapter(behaviorContext.world(), tempDir));
        IgnisStrategyRegistryImpl strategyRegistry = new IgnisStrategyRegistryImpl(
                new DefaultExplosionStrategy(behaviorContext.context().extensions(), new IgnisEventBusImpl()));
        strategyRegistry.register(
                IgnisStrategyDescriptor.of("storage", "Storage", "1.0.0", "test"),
                storageStrategy());

        PlacedBlockPersistenceService persistence = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir));
        CommonTestSupport.RecordingBlockVisualRenderer visualRenderer = new CommonTestSupport.RecordingBlockVisualRenderer();
        StrategyProfileResolver profileResolver = new StrategyProfileResolver(strategyRegistry);
        StrategyEventPublisher events = new StrategyEventPublisher(new IgnisEventBusImpl(), profileResolver);
        BlockManager blockManager = new BlockManager(
                new RuntimeBlockService(),
                behaviorContext.effects(),
                profileResolver,
                persistence,
                new CommonTestSupport.ImmediateIgnisScheduler(),
                visualRenderer,
                events,
                PerformanceSettings.defaults());

        List<dev.rono.igniscore.loader.LoadedExtension<BlockDefinition>> loaded = Arrays.stream(definitions)
                .map(definition -> {
                    try {
                        return CommonTestSupport.loadedBlock(definition);
                    } catch (Exception error) {
                        throw new IllegalStateException(error);
                    }
                })
                .toList();
        blockManager.loadFromExtensions(loaded);
        registerMissingBlockStrategies(strategyRegistry, behaviorContext, definitions);

        PdcBackedNbtService nbtService = new PdcBackedNbtService();
        BlockItemFactory blockItemFactory = new BlockItemFactory(blockManager, nbtService, platformHooks);
        ConfiguredEffectService effectService = new ConfiguredEffectService(plugin, platformHooks);
        CustomBlockBreakService breakService = new CustomBlockBreakService(
                plugin,
                blockManager,
                blockItemFactory,
                effectService,
                events,
                profileResolver);
        CustomBlockIgnitionService ignitionService = new CustomBlockIgnitionService(
                blockManager, breakService, effectService);
        BlockItemIdentifier blockItemIdentifier = new BlockItemIdentifier(plugin, nbtService);
        ItemIdentifier itemIdentifier = new ItemIdentifier(nbtService);
        CustomBlockPlacementService placementService = new CustomBlockPlacementService(
                plugin, blockManager, blockItemIdentifier, platformHooks);
        BlockListener blockListener = new BlockListener(
                blockManager,
                placementService,
                breakService,
                ignitionService,
                itemIdentifier,
                events);
        ExtensionSupportListener extensionSupportListener = new ExtensionSupportListener(
                blockManager, extensionSupport);

        return new Context(
                blockManager,
                breakService,
                ignitionService,
                blockItemFactory,
                blockItemIdentifier,
                itemIdentifier,
                strategyRegistry,
                effectService,
                extensionSupport,
                placementService,
                blockListener,
                extensionSupportListener,
                behaviorContext);
    }

    public static void placeCustomBlock(BlockManager blockManager, Block block, String typeId) {
        block.setType(Material.BARRIER);
        blockManager.registerPlacedBlock(BukkitBridge.toIgnis(block.getLocation()), typeId);
    }

    public static void performTicks(ServerMock server, long ticks) {
        BukkitSchedulerMock scheduler = (BukkitSchedulerMock) server.scheduler();
        scheduler.performTicks(ticks);
    }

    private static void registerMissingBlockStrategies(IgnisStrategyRegistryImpl strategyRegistry,
                                                     BehaviorTestSupport.TestContext behaviorContext,
                                                     BlockDefinition... definitions) {
        DefaultExplosionStrategy fallback = new DefaultExplosionStrategy(
                behaviorContext.context().extensions(), new IgnisEventBusImpl());
        for (BlockDefinition definition : definitions) {
            String extensionId = definition.getExtensionId();
            if (!strategyRegistry.isRegistered(extensionId)) {
                strategyRegistry.register(
                        IgnisStrategyDescriptor.of(extensionId, extensionId, "1.0.0", "test"),
                        fallback);
            }
        }
    }

    private static AbstractIgnisBlockStrategy storageStrategy() {
        return new AbstractIgnisBlockStrategy(IgnisStrategyDescriptor.of("storage", "Storage", "1.0.0", "test")) {
            @Override
            public StrategyProfile profile(BlockDefinition definition) {
                return StrategyProfile.builder()
                        .combustible(false)
                        .leftClickAction(CustomBlockAction.BREAK)
                        .rightClickAction(CustomBlockAction.NONE)
                        .build();
            }
        };
    }
}
