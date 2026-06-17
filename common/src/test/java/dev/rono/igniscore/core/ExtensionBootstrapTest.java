package dev.rono.igniscore.core;

import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionBootstrapTest {
    @TempDir
    Path tempDir;

    private RecordingBlockLoader blockLoader;
    private RecordingItemLoader itemLoader;
    private RecordingBlockTypeRegistry blockTypeRegistry;
    private RecordingItemManager itemManager;
    private RecordingBlockManager blockManager;
    private RecordingExtensionSupport extensionSupport;
    private RecordingBundledExtractor bundledExtractor;
    private ExtensionBootstrap bootstrap;

    @BeforeEach
    void setUp() throws Exception {
        blockLoader = new RecordingBlockLoader();
        itemLoader = new RecordingItemLoader();
        blockTypeRegistry = new RecordingBlockTypeRegistry();
        itemManager = new RecordingItemManager();
        blockManager = new RecordingBlockManager();
        extensionSupport = new RecordingExtensionSupport();
        bundledExtractor = new RecordingBundledExtractor();
        bootstrap = new ExtensionBootstrap(
                CommonTestSupport.runtimeHost(tempDir),
                bundledExtractor,
                blockLoader,
                itemLoader,
                blockTypeRegistry,
                itemManager,
                extensionSupport,
                blockManager);
    }

    @Test
    void prepareForReloadUnloadsRequestedKinds() throws Exception {
        blockLoader.loaded.add(sampleBlockExtension("block-a"));
        itemLoader.loaded.add(sampleItemExtension("item-a"));

        bootstrap.prepareForReload(ExtensionReloadScope.ALL);

        assertTrue(extensionSupport.cleared);
        assertTrue(blockManager.stoppedActiveBlocks);
        assertEquals(1, blockLoader.unloadCount);
        assertEquals(1, itemLoader.unloadCount);
    }

    @Test
    void prepareForReloadBlocksScopeOnlyUnloadsBlocks() throws Exception {
        bootstrap.prepareForReload(ExtensionReloadScope.BLOCKS);

        assertEquals(1, blockLoader.unloadCount);
        assertEquals(0, itemLoader.unloadCount);
        assertTrue(blockManager.stoppedActiveBlocks);
    }

    @Test
    void prepareForReloadItemsScopeOnlyUnloadsItems() {
        bootstrap.prepareForReload(ExtensionReloadScope.ITEMS);

        assertEquals(0, blockLoader.unloadCount);
        assertEquals(1, itemLoader.unloadCount);
        assertFalse(blockManager.stoppedActiveBlocks);
    }

    @Test
    void loadFreshExtractsAndLoadsRequestedKinds() throws Exception {
        blockLoader.freshResult = List.of(sampleBlockExtension("block-a"));
        itemLoader.freshResult = List.of(sampleItemExtension("item-a"));

        ExtensionLoadResult result = bootstrap.loadFresh(ExtensionReloadScope.ALL);

        assertEquals(1, bundledExtractor.extractCount.get());
        assertEquals(1, blockLoader.loadFreshCount);
        assertEquals(1, itemLoader.loadFreshCount);
        assertEquals(1, result.blocks().size());
        assertEquals(1, result.items().size());
    }

    @Test
    void commitReloadRegistersDefinitionsAndRefreshesVisuals() throws Exception {
        var blocks = List.of(sampleBlockExtension("block-a"));
        var items = List.of(sampleItemExtension("item-a"));

        bootstrap.commitReload(ExtensionReloadScope.ALL, new ExtensionLoadResult(blocks, items));

        assertEquals(blocks, blockLoader.committed);
        assertEquals(items, itemLoader.committed);
        assertEquals(blocks, blockTypeRegistry.loaded);
        assertEquals(items, itemManager.loaded);
        assertTrue(blockManager.refreshedVisuals);
    }

    @Test
    void loadAllDoesNotExtractTwice() throws Exception {
        blockLoader.freshResult = List.of(sampleBlockExtension("block-a"));
        itemLoader.freshResult = List.of(sampleItemExtension("item-a"));

        bootstrap.loadAll();

        assertEquals(1, bundledExtractor.extractCount.get());
        assertEquals(1, blockLoader.loadFreshCount);
        assertEquals(1, itemLoader.loadFreshCount);
    }

    private static LoadedExtension<BlockDefinition> sampleBlockExtension(String id) throws Exception {
        BlockDefinition definition = new BlockDefinition(
                id, "paper", "carrot_on_a_stick", Component.text(id), List.of(),
                true, true, "top.png", "side.png", "bottom.png",
                java.util.Map.of(), java.util.Map.of(), java.util.Map.of(), java.util.Map.of(),
                10001, false, false, false, id);
        return CommonTestSupport.loadedBlock(definition);
    }

    private static LoadedExtension<ItemDefinition> sampleItemExtension(String id) throws Exception {
        ItemDefinition definition = ItemDefinition.builder(id)
                .baseMaterial("paper")
                .customModelData(10001)
                .extensionId(id)
                .iconTexture("icon.png")
                .build();
        return new LoadedExtension<>(
                dev.rono.igniscore.api.extension.ExtensionManifest.fromStream(
                        new java.io.ByteArrayInputStream(("""
                                id: %s
                                name: Test
                                version: 1.0.0
                                api-version: 1.0.0
                                strategy: dev.example.Strategy
                                """.formatted(id)).getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                        "item-extension.yml"),
                new java.io.File("test.jar"),
                new java.net.URLClassLoader(new java.net.URL[]{new java.io.File(".").toURI().toURL()}),
                definition,
                new dev.rono.igniscore.api.extension.ExtensionResources(
                        new java.net.URLClassLoader(new java.net.URL[]{new java.io.File(".").toURI().toURL()})));
    }

    private static final class RecordingBlockLoader extends dev.rono.igniscore.loader.BlockExtensionLoader {
        final List<LoadedExtension<BlockDefinition>> loaded = new ArrayList<>();
        List<LoadedExtension<BlockDefinition>> freshResult = List.of();
        List<LoadedExtension<BlockDefinition>> committed = List.of();
        int unloadCount;
        int loadFreshCount;

        RecordingBlockLoader() {
            super(null, null);
        }

        @Override
        public List<LoadedExtension<BlockDefinition>> loadFresh() {
            loadFreshCount++;
            return freshResult;
        }

        @Override
        public void commitLoaded(List<LoadedExtension<BlockDefinition>> extensions) {
            committed = extensions;
            loaded.clear();
            loaded.addAll(extensions);
        }

        @Override
        public void unloadAll() {
            unloadCount++;
            loaded.clear();
        }
    }

    private static final class RecordingItemLoader extends dev.rono.igniscore.loader.ItemExtensionLoader {
        final List<LoadedExtension<ItemDefinition>> loaded = new ArrayList<>();
        List<LoadedExtension<ItemDefinition>> freshResult = List.of();
        List<LoadedExtension<ItemDefinition>> committed = List.of();
        int unloadCount;
        int loadFreshCount;

        RecordingItemLoader() {
            super(null, null);
        }

        @Override
        public List<LoadedExtension<ItemDefinition>> loadFresh() {
            loadFreshCount++;
            return freshResult;
        }

        @Override
        public void commitLoaded(List<LoadedExtension<ItemDefinition>> extensions) {
            committed = extensions;
            loaded.clear();
            loaded.addAll(extensions);
        }

        @Override
        public void unloadAll() {
            unloadCount++;
            loaded.clear();
        }
    }

    private static final class RecordingBlockTypeRegistry implements BlockTypeRegistry {
        List<LoadedExtension<BlockDefinition>> loaded = List.of();

        @Override
        public void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
            loaded = extensions;
        }
    }

    private static final class RecordingItemManager extends ItemManager {
        List<LoadedExtension<ItemDefinition>> loaded = List.of();

        @Override
        public void loadFromExtensions(List<LoadedExtension<ItemDefinition>> extensions) {
            loaded = extensions;
        }
    }

    private static final class RecordingBlockManager extends BlockManager {
        boolean stoppedActiveBlocks;
        boolean refreshedVisuals;

        RecordingBlockManager() {
            super(null, null, null, null, null, null, PerformanceSettings.defaults());
        }

        @Override
        public void stopActiveBlocks() {
            stoppedActiveBlocks = true;
        }

        @Override
        public void refreshPlacedBlockVisuals() {
            refreshedVisuals = true;
        }
    }

    private static final class RecordingExtensionSupport extends ExtensionSupportService {
        boolean cleared;

        RecordingExtensionSupport() {
            super(null);
        }

        @Override
        public void clear() {
            cleared = true;
        }
    }

    private static final class RecordingBundledExtractor extends dev.rono.igniscore.loader.BundledExtensionExtractor {
        final AtomicInteger extractCount = new AtomicInteger();

        RecordingBundledExtractor() {
            super(null);
        }

        @Override
        public void extractAll() {
            extractCount.incrementAndGet();
        }
    }
}
