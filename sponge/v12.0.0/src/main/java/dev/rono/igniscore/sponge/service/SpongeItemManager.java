package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.sponge.loader.SpongeLoadedExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpongeItemManager {
    private final Map<String, ItemDefinition> itemTypes = new HashMap<>();

    @Inject
    public SpongeItemManager() {
    }

    public void loadFromExtensions(List<SpongeLoadedExtension<ItemDefinition>> extensions) {
        itemTypes.clear();
        for (SpongeLoadedExtension<ItemDefinition> extension : extensions) {
            itemTypes.put(extension.getDefinition().getId(), extension.getDefinition());
        }
    }

    public Map<String, ItemDefinition> getItemTypes() {
        return Collections.unmodifiableMap(itemTypes);
    }
}
