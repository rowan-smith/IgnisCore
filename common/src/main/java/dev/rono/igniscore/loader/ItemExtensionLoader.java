package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ItemExtensionLoader {
    private final ExtensionLoadEngine engine;
    private final ExtensionResourceProvider resourceProvider;
    private final List<LoadedExtension<ItemDefinition>> loadedExtensions = new ArrayList<>();

    @Inject
    public ItemExtensionLoader(ExtensionLoadEngine engine, ExtensionResourceProvider resourceProvider) {
        this.engine = engine;
        this.resourceProvider = resourceProvider;
    }

    public List<LoadedExtension<ItemDefinition>> loadAll() {
        unloadAll();
        loadedExtensions.addAll(engine.loadItems());
        resourceProvider.setItemExtensions(loadedExtensions);
        return List.copyOf(loadedExtensions);
    }

    public void unloadAll() {
        engine.unload(loadedExtensions);
        loadedExtensions.clear();
        resourceProvider.setItemExtensions(List.of());
    }

    public List<LoadedExtension<ItemDefinition>> getLoadedExtensions() {
        return List.copyOf(loadedExtensions);
    }
}
