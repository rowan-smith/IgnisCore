package dev.rono.igniscore.sponge.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class SpongeBlockExtensionLoader {
    private final SpongeExtensionLoadEngine engine;
    private final List<SpongeLoadedExtension<BlockDefinition>> loadedExtensions = new ArrayList<>();

    @Inject
    public SpongeBlockExtensionLoader(SpongeExtensionLoadEngine engine) {
        this.engine = engine;
    }

    public List<SpongeLoadedExtension<BlockDefinition>> loadAll() {
        unloadAll();
        loadedExtensions.addAll(engine.loadBlocks());
        return List.copyOf(loadedExtensions);
    }

    public void unloadAll() {
        engine.unload(loadedExtensions);
        loadedExtensions.clear();
    }

    public List<SpongeLoadedExtension<BlockDefinition>> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }
}
