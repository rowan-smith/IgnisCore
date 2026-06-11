package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.model.BlockDefinition;

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
        loadedExtensions.addAll(engine.loadBlocks());
        resourceProvider.setBlockExtensions(loadedExtensions);
        return List.copyOf(loadedExtensions);
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
