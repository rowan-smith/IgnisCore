package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.config.ExtensionConfig;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public final class ItemDefinition implements ExtensionDefinition {
    private final String id;
    private final String baseMaterial;
    private final Component title;
    private final List<Component> description;
    private final Map<String, Object> customData;
    private final Map<String, Object> interactionSettings;
    private final int customModelData;
    private final String extensionId;
    private final String iconTexture;

    public ItemDefinition(String id, String baseMaterial, Component title, List<Component> description,
                          Map<String, Object> customData,
                          Map<String, Object> interactionSettings, int customModelData, String extensionId,
                          String iconTexture) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.title = title;
        this.description = List.copyOf(description);
        this.customData = customData == null ? Map.of() : Map.copyOf(customData);
        this.interactionSettings = interactionSettings == null ? Map.of() : Map.copyOf(interactionSettings);
        this.customModelData = customModelData;
        this.extensionId = extensionId;
        this.iconTexture = iconTexture;
    }

    private ItemDefinition(Builder builder) {
        this(builder.id, builder.baseMaterial, builder.title, builder.description, builder.customData,
                builder.interactionSettings, builder.customModelData, builder.extensionId, builder.iconTexture);
    }

    public static Builder builder(String id) {
        return new Builder(id);
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

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public Map<String, Object> getInteractionSettings() {
        return interactionSettings;
    }

    /** Typed view of {@link #getCustomData()}. */
    public ExtensionConfig getCustomConfig() {
        return ExtensionConfig.of(customData);
    }

    /** Typed view of {@link #getInteractionSettings()}. */
    public ExtensionConfig getInteractionConfig() {
        return ExtensionConfig.of(interactionSettings);
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getExtensionId() {
        return extensionId;
    }

    public String getIconTexture() {
        return iconTexture;
    }

    public static final class Builder {
        private final String id;
        private String baseMaterial = "paper";
        private Component title;
        private List<Component> description = List.of();
        private Map<String, Object> customData = Map.of();
        private Map<String, Object> interactionSettings = Map.of();
        private int customModelData = 20001;
        private String extensionId;
        private String iconTexture = "icon.png";

        private Builder(String id) {
            this.id = id;
            this.title = Component.text(id);
            this.extensionId = id;
        }

        public Builder baseMaterial(String baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder description(List<Component> description) {
            this.description = description;
            return this;
        }

        public Builder customData(Map<String, Object> customData) {
            this.customData = customData;
            return this;
        }

        public Builder interactionSettings(Map<String, Object> interactionSettings) {
            this.interactionSettings = interactionSettings;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder extensionId(String extensionId) {
            this.extensionId = extensionId;
            return this;
        }

        public Builder iconTexture(String iconTexture) {
            this.iconTexture = iconTexture;
            return this;
        }

        public ItemDefinition build() {
            return new ItemDefinition(this);
        }
    }
}
