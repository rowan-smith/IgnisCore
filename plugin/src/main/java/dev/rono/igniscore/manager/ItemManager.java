package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.loader.LoadedItemExtension;
import dev.rono.igniscore.model.ItemDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    private final Map<String, ItemDefinition> itemTypes = new HashMap<>();

    @Inject
    public ItemManager() {
    }

    public void loadFromExtensions(List<LoadedItemExtension> extensions) {
        itemTypes.clear();
        for (LoadedItemExtension extension : extensions) {
            ItemDefinition definition = extension.getItemDefinition();
            itemTypes.put(definition.getId(), definition);
        }
    }

    public Map<String, ItemDefinition> getItemTypes() {
        return Collections.unmodifiableMap(itemTypes);
    }
}
