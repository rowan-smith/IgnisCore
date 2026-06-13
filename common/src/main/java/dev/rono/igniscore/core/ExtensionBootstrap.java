package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.BundledExtensionExtractor;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.service.ExtensionSupportService;

@Singleton
public class ExtensionBootstrap {
    private final IgnisRuntimeHost host;
    private final BundledExtensionExtractor bundledExtractor;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final BlockTypeRegistry blockTypeRegistry;
    private final ItemManager itemManager;
    private final ExtensionSupportService extensionSupportService;
    private final BlockManager blockManager;

    @Inject
    public ExtensionBootstrap(IgnisRuntimeHost host,
                              BundledExtensionExtractor bundledExtractor,
                              BlockExtensionLoader blockExtensionLoader,
                              ItemExtensionLoader itemExtensionLoader,
                              BlockTypeRegistry blockTypeRegistry,
                              ItemManager itemManager,
                              ExtensionSupportService extensionSupportService,
                              BlockManager blockManager) {
        this.host = host;
        this.bundledExtractor = bundledExtractor;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockTypeRegistry = blockTypeRegistry;
        this.itemManager = itemManager;
        this.extensionSupportService = extensionSupportService;
        this.blockManager = blockManager;
    }

    public void loadAll() {
        bundledExtractor.extractAll();
        commitReload(ExtensionReloadScope.ALL, loadFresh(ExtensionReloadScope.ALL));
    }

    public void reloadAll() {
        prepareForReload(ExtensionReloadScope.ALL);
        commitReload(ExtensionReloadScope.ALL, loadFresh(ExtensionReloadScope.ALL));
    }

    public void reloadBlocks() {
        prepareForReload(ExtensionReloadScope.BLOCKS);
        commitReload(ExtensionReloadScope.BLOCKS, loadFresh(ExtensionReloadScope.BLOCKS));
    }

    public void reloadItems() {
        prepareForReload(ExtensionReloadScope.ITEMS);
        commitReload(ExtensionReloadScope.ITEMS, loadFresh(ExtensionReloadScope.ITEMS));
    }

    public void prepareForReload(ExtensionReloadScope scope) {
        extensionSupportService.clear();
        if (scope == ExtensionReloadScope.ALL || scope == ExtensionReloadScope.BLOCKS) {
            blockManager.stopActiveBlocks();
        }
        if (scope == ExtensionReloadScope.ALL || scope == ExtensionReloadScope.BLOCKS) {
            blockExtensionLoader.unloadAll();
        }
        if (scope == ExtensionReloadScope.ALL || scope == ExtensionReloadScope.ITEMS) {
            itemExtensionLoader.unloadAll();
        }
    }

    public ExtensionLoadResult loadFresh(ExtensionReloadScope scope) {
        bundledExtractor.extractAll();
        var blocks = scope == ExtensionReloadScope.ALL || scope == ExtensionReloadScope.BLOCKS
                ? blockExtensionLoader.loadFresh()
                : null;
        var items = scope == ExtensionReloadScope.ALL || scope == ExtensionReloadScope.ITEMS
                ? itemExtensionLoader.loadFresh()
                : null;
        return new ExtensionLoadResult(blocks, items);
    }

    public void commitReload(ExtensionReloadScope scope, ExtensionLoadResult result) {
        if (result.blocks() != null) {
            blockExtensionLoader.commitLoaded(result.blocks());
            blockTypeRegistry.loadFromExtensions(result.blocks());
            host.getLogger().info("Loaded " + result.blocks().size() + " block extension(s).");
            blockManager.refreshPlacedBlockVisuals();
        }
        if (result.items() != null) {
            itemExtensionLoader.commitLoaded(result.items());
            itemManager.loadFromExtensions(result.items());
            host.getLogger().info("Loaded " + result.items().size() + " item extension(s).");
        }
    }
}
