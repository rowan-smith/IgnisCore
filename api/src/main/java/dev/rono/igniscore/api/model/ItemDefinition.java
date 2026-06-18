package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.config.ExtensionConfig;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

/**
 * Immutable definition of a custom item loaded from extension {@code config.yml}.
 *
 * <p>Captures display text, base material, icon texture, custom model data, and YAML sections
 * ({@code custom_data}, {@code behavior}, {@code interactions}). The {@link #getId() config id}
 * is the in-game type id; {@link #getExtensionId()} matches the manifest strategy registry key.</p>
 *
 * @see ExtensionDefinition
 */
public final class ItemDefinition implements ExtensionDefinition {
    private final String id;
    private final String baseMaterial;
    private final Component title;
    private final List<Component> description;
    private final Map<String, Object> customData;
    private final Map<String, Object> behaviorSettings;
    private final Map<String, Object> interactionSettings;
    private final int customModelData;
    private final String extensionId;
    private final String iconTexture;
    private final String textureFallback;

    /**
     * Creates a fully specified item definition.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material key for the item stack
     * @param title display name
     * @param description lore lines
     * @param customData {@code custom_data} YAML section
     * @param behaviorSettings {@code behavior} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param customModelData resource-pack custom model data id
     * @param extensionId manifest strategy registry id
     * @param iconTexture icon texture path relative to the extension JAR
     * @param textureFallback optional {@code textures.fallback} reference string
     */
    public ItemDefinition(String id, String baseMaterial, Component title, List<Component> description,
                          Map<String, Object> customData,
                          Map<String, Object> behaviorSettings,
                          Map<String, Object> interactionSettings, int customModelData, String extensionId,
                          String iconTexture, String textureFallback) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.title = title;
        this.description = List.copyOf(description);
        this.customData = customData == null ? Map.of() : Map.copyOf(customData);
        this.behaviorSettings = behaviorSettings == null ? Map.of() : Map.copyOf(behaviorSettings);
        this.interactionSettings = interactionSettings == null ? Map.of() : Map.copyOf(interactionSettings);
        this.customModelData = customModelData;
        this.extensionId = extensionId;
        this.iconTexture = iconTexture;
        this.textureFallback = textureFallback;
    }

    /**
     * Creates an item definition with a {@code behavior} section and no texture fallback.
     */
    public ItemDefinition(String id, String baseMaterial, Component title, List<Component> description,
                          Map<String, Object> customData,
                          Map<String, Object> behaviorSettings,
                          Map<String, Object> interactionSettings, int customModelData, String extensionId,
                          String iconTexture) {
        this(id, baseMaterial, title, description, customData, behaviorSettings, interactionSettings,
                customModelData, extensionId, iconTexture, null);
    }

    /**
     * Creates an item definition without an explicit {@code behavior} section.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material key for the item stack
     * @param title display name
     * @param description lore lines
     * @param customData {@code custom_data} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param customModelData resource-pack custom model data id
     * @param extensionId manifest strategy registry id
     * @param iconTexture icon texture path relative to the extension JAR
     */
    public ItemDefinition(String id, String baseMaterial, Component title, List<Component> description,
                          Map<String, Object> customData,
                          Map<String, Object> interactionSettings, int customModelData, String extensionId,
                          String iconTexture) {
        this(id, baseMaterial, title, description, customData, Map.of(), interactionSettings,
                customModelData, extensionId, iconTexture, null);
    }

    private ItemDefinition(Builder builder) {
        this(builder.id, builder.baseMaterial, builder.title, builder.description, builder.customData,
                builder.behaviorSettings, builder.interactionSettings, builder.customModelData, builder.extensionId,
                builder.iconTexture, builder.textureFallback);
    }

    /**
     * @param id in-game type id used to seed builder defaults
     * @return fluent builder for test and programmatic construction
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }

    /** {@inheritDoc} */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return vanilla material key for the item stack
     */
    public String getBaseMaterial() {
        return baseMaterial;
    }

    /**
     * @return display name component
     */
    public Component getTitle() {
        return title;
    }

    /**
     * @return immutable lore lines
     */
    public List<Component> getDescription() {
        return description;
    }

    /**
     * @return unmodifiable {@code custom_data} YAML map
     */
    public Map<String, Object> getCustomData() {
        return customData;
    }

    /**
     * @return unmodifiable {@code behavior} YAML map
     */
    public Map<String, Object> getBehaviorSettings() {
        return behaviorSettings;
    }

    /**
     * @return unmodifiable flattened {@code interactions} YAML map
     */
    public Map<String, Object> getInteractionSettings() {
        return interactionSettings;
    }

    /**
     * @return typed view of {@link #getCustomData()}
     */
    public ExtensionConfig getCustomConfig() {
        return ExtensionConfig.of(customData);
    }

    /**
     * @return typed view of {@link #getBehaviorSettings()}
     */
    public ExtensionConfig getBehaviorConfig() {
        return ExtensionConfig.of(behaviorSettings);
    }

    /**
     * @return typed view of {@link #getInteractionSettings()}
     */
    public ExtensionConfig getInteractionConfig() {
        return ExtensionConfig.of(interactionSettings);
    }

    /**
     * @return resource-pack custom model data id
     */
    public int getCustomModelData() {
        return customModelData;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionId() {
        return extensionId;
    }

    /**
     * @return icon texture path relative to the extension JAR
     */
    public String getIconTexture() {
        return iconTexture;
    }

    /**
     * @return raw {@code textures.fallback} value, or {@code null} when unset
     */
    public String getTextureFallback() {
        return textureFallback;
    }

    /**
     * @return parsed {@code textures.fallback} reference, or {@code null} when unset
     */
    public TextureFallbackReference getTextureFallbackReference() {
        return TextureFallbackReference.parse(textureFallback);
    }

    /**
     * Fluent builder for {@link ItemDefinition}, primarily used in tests and samples.
     */
    public static final class Builder {
        private final String id;
        private String baseMaterial = "paper";
        private Component title;
        private List<Component> description = List.of();
        private Map<String, Object> customData = Map.of();
        private Map<String, Object> behaviorSettings = Map.of();
        private Map<String, Object> interactionSettings = Map.of();
        private int customModelData = 20001;
        private String extensionId;
        private String iconTexture = "icon.png";
        private String textureFallback;

        private Builder(String id) {
            this.id = id;
            this.title = Component.text(id);
            this.extensionId = id;
        }

        /**
         * @param baseMaterial vanilla material key for the item stack
         * @return this builder
         */
        public Builder baseMaterial(String baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        /**
         * @param title display name component
         * @return this builder
         */
        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        /**
         * @param description lore lines
         * @return this builder
         */
        public Builder description(List<Component> description) {
            this.description = description;
            return this;
        }

        /**
         * @param customData {@code custom_data} YAML map
         * @return this builder
         */
        public Builder customData(Map<String, Object> customData) {
            this.customData = customData;
            return this;
        }

        /**
         * @param behaviorSettings {@code behavior} YAML map
         * @return this builder
         */
        public Builder behaviorSettings(Map<String, Object> behaviorSettings) {
            this.behaviorSettings = behaviorSettings;
            return this;
        }

        /**
         * @param interactionSettings flattened {@code interactions} YAML map
         * @return this builder
         */
        public Builder interactionSettings(Map<String, Object> interactionSettings) {
            this.interactionSettings = interactionSettings;
            return this;
        }

        /**
         * @param customModelData resource-pack custom model data id
         * @return this builder
         */
        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        /**
         * @param extensionId manifest strategy registry id
         * @return this builder
         */
        public Builder extensionId(String extensionId) {
            this.extensionId = extensionId;
            return this;
        }

        /**
         * @param iconTexture icon texture path relative to the extension JAR
         * @return this builder
         */
        public Builder iconTexture(String iconTexture) {
            this.iconTexture = iconTexture;
            return this;
        }

        /**
         * @param textureFallback raw {@code textures.fallback} value
         * @return this builder
         */
        public Builder textureFallback(String textureFallback) {
            this.textureFallback = textureFallback;
            return this;
        }

        /**
         * @return immutable item definition
         */
        public ItemDefinition build() {
            return new ItemDefinition(this);
        }
    }
}
