package dev.rono.igniscore.sponge.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class SpongeItemExtensionLoader {
    private final SpongeExtensionLoadEngine engine;
    private final List<SpongeLoadedExtension<ItemDefinition>> loadedExtensions = new ArrayList<>();

    @Inject
    public SpongeItemExtensionLoader(SpongeExtensionLoadEngine engine) {
        this.engine = engine;
    }

    public List<SpongeLoadedExtension<ItemDefinition>> loadAll() {
        unloadAll();
        loadedExtensions.addAll(engine.loadItems());
        return List.copyOf(loadedExtensions);
    }

    public void unloadAll() {
        engine.unload(loadedExtensions);
        loadedExtensions.clear();
    }

    public List<SpongeLoadedExtension<ItemDefinition>> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }
}
