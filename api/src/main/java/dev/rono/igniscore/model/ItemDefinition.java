package dev.rono.igniscore.model;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class ItemDefinition {
    private final String id;
    private final String baseMaterial;
    private final Component title;
    private final List<Component> description;
    private final String strategy;
    private final Map<String, Object> customData;
    private final Map<String, Object> interactionSettings;
    private final int customModelData;
    private final String extensionId;

    public ItemDefinition(String id, String baseMaterial, Component title, List<Component> description,
                          String strategy, Map<String, Object> customData,
                          Map<String, Object> interactionSettings, int customModelData, String extensionId) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.title = title;
        this.description = description;
        this.strategy = strategy;
        this.customData = customData;
        this.interactionSettings = interactionSettings;
        this.customModelData = customModelData;
        this.extensionId = extensionId;
    }

    public String getId() {
        return id;
    }

    public String getBaseMaterial() {
        return baseMaterial;
    }

    public Component getTitle() {
        return title;
    }

    public List<Component> getDescription() {
        return description;
    }

    public String getStrategy() {
        return strategy;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public Map<String, Object> getInteractionSettings() {
        return interactionSettings;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getExtensionId() {
        return extensionId;
    }
}
