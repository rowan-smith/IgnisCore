package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.config.ExtensionConfig;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

/**
 * Immutable definition of a custom block loaded from extension {@code config.yml}.
 *
 * <p>Captures display text, materials, textures, placement rules, animation flags, and YAML
 * sections ({@code custom_data}, {@code behavior}, {@code interactions}, breaking settings).
 * The {@link #getId() config id} is the in-game type id; {@link #getExtensionId()} matches the
 * manifest strategy registry key.</p>
 *
 * @see ExtensionDefinition
 */
public final class BlockDefinition implements ExtensionDefinition {
    private final String id;
    private final String baseMaterial;
    private final String renderMaterial;
    private final Component title;
    private final List<Component> description;

    private final boolean placeable;
    private final boolean breakable;

    private final String topTexture;
    private final String sideTexture;
    private final String bottomTexture;
    private final String side1Texture;
    private final String side2Texture;
    private final String side3Texture;
    private final String side4Texture;
    private final String textureFallback;

    private final Map<String, Object> customData;
    private final Map<String, Object> breakSettings;
    private final Map<String, Object> behaviorSettings;
    private final Map<String, Object> interactionSettings;
    private final Map<String, Object> displaySettings;

    private final int customModelData;
    private final String extensionId;

    private final boolean rotate;
    private final boolean floatBob;
    private final boolean pulse;

    /**
     * Creates a block definition without explicit {@code behavior} or per-side texture overrides.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, Map.of(), interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse);
    }

    /**
     * Creates a block definition with a {@code behavior} section and default extension id {@code builtin}.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param behaviorSettings {@code behavior} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> behaviorSettings,
                           Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, behaviorSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, "builtin", null, null, null, null, null);
    }

    /**
     * Creates a block definition with an explicit extension id and no per-side texture overrides.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     * @param extensionId manifest strategy registry id
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, Map.of(), interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId);
    }

    /**
     * Creates a block definition with behavior settings and an explicit extension id.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param behaviorSettings {@code behavior} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     * @param extensionId manifest strategy registry id
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> behaviorSettings,
                           Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, behaviorSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId, null, null, null, null, null);
    }

    /**
     * Creates a block definition with per-side texture overrides and default extension id {@code builtin}.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     * @param extensionId manifest strategy registry id
     * @param side1Texture optional texture for horizontal side face 1
     * @param side2Texture optional texture for horizontal side face 2
     * @param side3Texture optional texture for horizontal side face 3
     * @param side4Texture optional texture for horizontal side face 4
     * @param textureFallback optional {@code textures.fallback} reference string
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId, String side1Texture, String side2Texture, String side3Texture, String side4Texture,
                           String textureFallback) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, Map.of(), interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId, side1Texture, side2Texture, side3Texture, side4Texture,
                textureFallback);
    }

    /**
     * Creates a block definition with per-side texture overrides and default extension id {@code builtin}.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     * @param extensionId manifest strategy registry id
     * @param side1Texture optional texture for horizontal side face 1
     * @param side2Texture optional texture for horizontal side face 2
     * @param side3Texture optional texture for horizontal side face 3
     * @param side4Texture optional texture for horizontal side face 4
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId, String side1Texture, String side2Texture, String side3Texture, String side4Texture) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId, side1Texture, side2Texture, side3Texture, side4Texture,
                null);
    }

    /**
     * Creates a fully specified block definition.
     *
     * @param id in-game type id
     * @param baseMaterial vanilla material used for the placed block item
     * @param renderMaterial vanilla material used for the in-world display entity
     * @param title display name
     * @param description lore lines
     * @param placeable whether players may place this block
     * @param breakable whether players may break this block
     * @param topTexture top face texture path
     * @param sideTexture default side face texture path
     * @param bottomTexture bottom face texture path
     * @param customData {@code custom_data} YAML section
     * @param breakSettings {@code block.breaking} YAML section
     * @param behaviorSettings {@code behavior} YAML section
     * @param interactionSettings flattened {@code interactions} section
     * @param displaySettings flattened {@code block_display} section
     * @param customModelData resource-pack custom model data id
     * @param rotate enable display rotation animation
     * @param floatBob enable display bob animation
     * @param pulse enable display pulse animation
     * @param extensionId manifest strategy registry id
     * @param side1Texture optional texture for horizontal side face 1
     * @param side2Texture optional texture for horizontal side face 2
     * @param side3Texture optional texture for horizontal side face 3
     * @param side4Texture optional texture for horizontal side face 4
     * @param textureFallback optional {@code textures.fallback} reference string
     */
    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> behaviorSettings,
                           Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId, String side1Texture, String side2Texture, String side3Texture, String side4Texture,
                           String textureFallback) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.renderMaterial = renderMaterial;
        this.title = title;
        this.description = List.copyOf(description);
        this.placeable = placeable;
        this.breakable = breakable;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.side1Texture = side1Texture;
        this.side2Texture = side2Texture;
        this.side3Texture = side3Texture;
        this.side4Texture = side4Texture;
        this.textureFallback = textureFallback;
        this.customData = customData == null ? Map.of() : Map.copyOf(customData);
        this.breakSettings = breakSettings == null ? Map.of() : Map.copyOf(breakSettings);
        this.behaviorSettings = behaviorSettings == null ? Map.of() : Map.copyOf(behaviorSettings);
        this.interactionSettings = interactionSettings == null ? Map.of() : Map.copyOf(interactionSettings);
        this.displaySettings = displaySettings == null ? Map.of() : Map.copyOf(displaySettings);
        this.customModelData = customModelData;
        this.extensionId = extensionId != null ? extensionId : "builtin";
        this.rotate = rotate;
        this.floatBob = floatBob;
        this.pulse = pulse;
    }

    private BlockDefinition(Builder builder) {
        this(builder.id, builder.baseMaterial, builder.renderMaterial, builder.title, builder.description,
                builder.placeable, builder.breakable, builder.topTexture, builder.sideTexture, builder.bottomTexture,
                builder.customData, builder.breakSettings, builder.behaviorSettings, builder.interactionSettings,
                builder.displaySettings,
                builder.customModelData, builder.rotate, builder.floatBob, builder.pulse, builder.extensionId,
                builder.side1Texture, builder.side2Texture, builder.side3Texture, builder.side4Texture,
                builder.textureFallback);
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
    public String getId() { return id; }

    /**
     * @return vanilla material key for the placed block item
     */
    public String getBaseMaterial() { return baseMaterial; }

    /**
     * @return vanilla material key for the in-world display entity, falling back to {@link #getBaseMaterial()}
     */
    public String getRenderMaterial() { return renderMaterial != null ? renderMaterial : baseMaterial; }

    /**
     * @return display name component
     */
    public Component getTitle() { return title; }

    /**
     * @return immutable lore lines
     */
    public List<Component> getDescription() { return description; }

    /**
     * @return {@code true} when players may place this block
     */
    public boolean isPlaceable() { return placeable; }

    /**
     * @return {@code true} when players may break this block
     */
    public boolean isBreakable() { return breakable; }

    /**
     * @return top face texture path relative to the extension JAR
     */
    public String getTopTexture() { return topTexture; }

    /**
     * @return default side face texture path
     */
    public String getSideTexture() { return sideTexture; }

    /**
     * @return bottom face texture path
     */
    public String getBottomTexture() { return bottomTexture; }

    /**
     * @return per-side texture for horizontal face 1, or {@code null} to use {@link #getSideTexture()}
     */
    public String getSide1Texture() { return side1Texture; }

    /**
     * @return per-side texture for horizontal face 2, or {@code null} to use {@link #getSideTexture()}
     */
    public String getSide2Texture() { return side2Texture; }

    /**
     * @return per-side texture for horizontal face 3, or {@code null} to use {@link #getSideTexture()}
     */
    public String getSide3Texture() { return side3Texture; }

    /**
     * @return per-side texture for horizontal face 4, or {@code null} to use {@link #getSideTexture()}
     */
    public String getSide4Texture() { return side4Texture; }

    /**
     * @return raw {@code textures.fallback} value, or {@code null} when unset
     */
    public String getTextureFallback() { return textureFallback; }

    /**
     * @return parsed {@code textures.fallback} reference, or {@code null} when unset
     */
    public TextureFallbackReference getTextureFallbackReference() {
        return TextureFallbackReference.parse(textureFallback);
    }

    /**
     * @return {@code true} when any per-side texture override is set
     */
    public boolean hasPerSideTextures() {
        return side1Texture != null || side2Texture != null || side3Texture != null || side4Texture != null;
    }

    /**
     * Resolves the texture for a horizontal side face, falling back to {@link #getSideTexture()}.
     *
     * @param face side index from 1 to 4
     * @return texture path for the face
     * @throws IllegalArgumentException when {@code face} is not between 1 and 4
     */
    public String getResolvedSideTexture(int face) {
        String texture = switch (face) {
            case 1 -> side1Texture;
            case 2 -> side2Texture;
            case 3 -> side3Texture;
            case 4 -> side4Texture;
            default -> throw new IllegalArgumentException("Side face must be 1-4, got: " + face);
        };
        return texture != null ? texture : sideTexture;
    }

    /**
     * @return unmodifiable {@code custom_data} YAML map
     */
    public Map<String, Object> getCustomData() { return customData; }

    /**
     * @return unmodifiable {@code block.breaking} YAML map
     */
    public Map<String, Object> getBreakSettings() { return breakSettings; }

    /**
     * @return unmodifiable {@code behavior} YAML map
     */
    public Map<String, Object> getBehaviorSettings() { return behaviorSettings; }

    /**
     * @return unmodifiable flattened {@code interactions} YAML map
     */
    public Map<String, Object> getInteractionSettings() { return interactionSettings; }

    /**
     * @return unmodifiable flattened {@code block_display} YAML map
     */
    public Map<String, Object> getDisplaySettings() { return displaySettings; }

    /**
     * @return typed view of {@link #getCustomData()}
     */
    public ExtensionConfig getCustomConfig() {
        return ExtensionConfig.of(customData);
    }

    /**
     * @return typed view of {@link #getBreakSettings()}
     */
    public ExtensionConfig getBreakConfig() {
        return ExtensionConfig.of(breakSettings);
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
    public int getCustomModelData() { return customModelData; }

    /** {@inheritDoc} */
    @Override
    public String getExtensionId() { return extensionId; }

    /**
     * @return {@code true} when display rotation animation is enabled
     */
    public boolean isRotate() { return rotate; }

    /**
     * @return {@code true} when display bob animation is enabled
     */
    public boolean isFloatBob() { return floatBob; }

    /**
     * @return {@code true} when display pulse animation is enabled
     */
    public boolean isPulse() { return pulse; }

    /**
     * Fluent builder for {@link BlockDefinition}, primarily used in tests and samples.
     */
    public static final class Builder {
        private final String id;
        private String baseMaterial = "paper";
        private String renderMaterial = "carrot_on_a_stick";
        private Component title;
        private List<Component> description = List.of();
        private boolean placeable = true;
        private boolean breakable = true;
        private String topTexture;
        private String sideTexture;
        private String bottomTexture;
        private String side1Texture;
        private String side2Texture;
        private String side3Texture;
        private String side4Texture;
        private String textureFallback;
        private Map<String, Object> customData = Map.of();
        private Map<String, Object> breakSettings = Map.of();
        private Map<String, Object> behaviorSettings = Map.of();
        private Map<String, Object> interactionSettings = Map.of();
        private Map<String, Object> displaySettings = Map.of();
        private int customModelData = 10001;
        private String extensionId = "builtin";
        private boolean rotate = true;
        private boolean floatBob = true;
        private boolean pulse = true;

        private Builder(String id) {
            this.id = id;
            this.title = Component.text(id);
            this.topTexture = id + "-top.png";
            this.sideTexture = id + "-side.png";
            this.bottomTexture = id + "-bottom.png";
        }

        /**
         * @param baseMaterial vanilla material key for the placed block item
         * @return this builder
         */
        public Builder baseMaterial(String baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        /**
         * @param renderMaterial vanilla material key for the in-world display entity
         * @return this builder
         */
        public Builder renderMaterial(String renderMaterial) {
            this.renderMaterial = renderMaterial;
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
         * @param placeable whether players may place this block
         * @return this builder
         */
        public Builder placeable(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        /**
         * @param breakable whether players may break this block
         * @return this builder
         */
        public Builder breakable(boolean breakable) {
            this.breakable = breakable;
            return this;
        }

        /**
         * @param top top face texture path
         * @param side default side face texture path
         * @param bottom bottom face texture path
         * @return this builder
         */
        public Builder textures(String top, String side, String bottom) {
            this.topTexture = top;
            this.sideTexture = side;
            this.bottomTexture = bottom;
            return this;
        }

        /**
         * @param side1 texture for horizontal side face 1
         * @param side2 texture for horizontal side face 2
         * @param side3 texture for horizontal side face 3
         * @param side4 texture for horizontal side face 4
         * @return this builder
         */
        public Builder sideTextures(String side1, String side2, String side3, String side4) {
            this.side1Texture = side1;
            this.side2Texture = side2;
            this.side3Texture = side3;
            this.side4Texture = side4;
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
         * @param customData {@code custom_data} YAML map
         * @return this builder
         */
        public Builder customData(Map<String, Object> customData) {
            this.customData = customData;
            return this;
        }

        /**
         * @param breakSettings {@code block.breaking} YAML map
         * @return this builder
         */
        public Builder breakSettings(Map<String, Object> breakSettings) {
            this.breakSettings = breakSettings;
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
         * @param displaySettings flattened {@code block_display} YAML map
         * @return this builder
         */
        public Builder displaySettings(Map<String, Object> displaySettings) {
            this.displaySettings = displaySettings;
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
         * @param rotate enable display rotation animation
         * @param floatBob enable display bob animation
         * @param pulse enable display pulse animation
         * @return this builder
         */
        public Builder animations(boolean rotate, boolean floatBob, boolean pulse) {
            this.rotate = rotate;
            this.floatBob = floatBob;
            this.pulse = pulse;
            return this;
        }

        /**
         * @return immutable block definition
         */
        public BlockDefinition build() {
            return new BlockDefinition(this);
        }
    }
}
