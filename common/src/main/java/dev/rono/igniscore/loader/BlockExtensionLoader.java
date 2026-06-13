package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class BlockExtensionLoader {
    private final ExtensionLoadEngine engine;
    private final ExtensionResourceProvider resourceProvider;
    private final List<LoadedExtension<BlockDefinition>> loadedExtensions = new ArrayList<>();

    @Inject
    public BlockExtensionLoader(ExtensionLoadEngine engine, ExtensionResourceProvider resourceProvider) {
        this.engine = engine;
        this.resourceProvider = resourceProvider;
    }

    public List<LoadedExtension<BlockDefinition>> loadAll() {
        unloadAll();
        List<LoadedExtension<BlockDefinition>> fresh = loadFresh();
        commitLoaded(fresh);
        return getLoadedExtensions();
    }

    public List<LoadedExtension<BlockDefinition>> loadFresh() {
        return List.copyOf(engine.loadBlocks());
    }

    public void commitLoaded(List<LoadedExtension<BlockDefinition>> extensions) {
        loadedExtensions.clear();
        loadedExtensions.addAll(extensions);
        resourceProvider.setBlockExtensions(loadedExtensions);
    }

    public void unloadAll() {
        engine.unload(loadedExtensions);
        loadedExtensions.clear();
        resourceProvider.setBlockExtensions(List.of());
    }

    public List<LoadedExtension<BlockDefinition>> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }
}
