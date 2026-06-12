package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    private final Map<String, ItemDefinition> itemTypes = new HashMap<>();

    @Inject
    public ItemManager() {
    }

    public void loadFromExtensions(List<LoadedExtension<ItemDefinition>> extensions) {
        itemTypes.clear();
        for (LoadedExtension<ItemDefinition> extension : extensions) {
            ItemDefinition definition = extension.getDefinition();
            itemTypes.put(definition.getId(), definition);
        }
    }

    public Map<String, ItemDefinition> getItemTypes() {
        return Collections.unmodifiableMap(itemTypes);
    }
}
