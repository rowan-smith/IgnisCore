package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.config.ExtensionConfig;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

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

    private final Map<String, Object> customData;
    private final Map<String, Object> breakSettings;
    private final Map<String, Object> interactionSettings;
    private final Map<String, Object> displaySettings;

    private final int customModelData;
    private final String extensionId;

    private final boolean rotate;
    private final boolean floatBob;
    private final boolean pulse;

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, "builtin", null, null, null, null);
    }

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, customData, breakSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId, null, null, null, null);
    }

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId, String side1Texture, String side2Texture, String side3Texture, String side4Texture) {
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
        this.customData = customData == null ? Map.of() : Map.copyOf(customData);
        this.breakSettings = breakSettings == null ? Map.of() : Map.copyOf(breakSettings);
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
                builder.customData, builder.breakSettings, builder.interactionSettings, builder.displaySettings,
                builder.customModelData, builder.rotate, builder.floatBob, builder.pulse, builder.extensionId,
                builder.side1Texture, builder.side2Texture, builder.side3Texture, builder.side4Texture);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String getId() { return id; }
    public String getBaseMaterial() { return baseMaterial; }
    public String getRenderMaterial() { return renderMaterial != null ? renderMaterial : baseMaterial; }
    public Component getTitle() { return title; }
    public List<Component> getDescription() { return description; }
    public boolean isPlaceable() { return placeable; }
    public boolean isBreakable() { return breakable; }
    public String getTopTexture() { return topTexture; }
    public String getSideTexture() { return sideTexture; }
    public String getBottomTexture() { return bottomTexture; }
    public String getSide1Texture() { return side1Texture; }
    public String getSide2Texture() { return side2Texture; }
    public String getSide3Texture() { return side3Texture; }
    public String getSide4Texture() { return side4Texture; }

    public boolean hasPerSideTextures() {
        return side1Texture != null || side2Texture != null || side3Texture != null || side4Texture != null;
    }

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

    public Map<String, Object> getCustomData() { return customData; }
    public Map<String, Object> getBreakSettings() { return breakSettings; }
    public Map<String, Object> getInteractionSettings() { return interactionSettings; }
    public Map<String, Object> getDisplaySettings() { return displaySettings; }

    /** Typed view of {@link #getCustomData()}. */
    public ExtensionConfig getCustomConfig() {
        return ExtensionConfig.of(customData);
    }

    /** Typed view of {@link #getBreakSettings()}. */
    public ExtensionConfig getBreakConfig() {
        return ExtensionConfig.of(breakSettings);
    }

    /** Typed view of {@link #getInteractionSettings()}. */
    public ExtensionConfig getInteractionConfig() {
        return ExtensionConfig.of(interactionSettings);
    }

    public int getCustomModelData() { return customModelData; }
    public String getExtensionId() { return extensionId; }
    public boolean isRotate() { return rotate; }
    public boolean isFloatBob() { return floatBob; }
    public boolean isPulse() { return pulse; }

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
        private Map<String, Object> customData = Map.of();
        private Map<String, Object> breakSettings = Map.of();
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

        public Builder baseMaterial(String baseMaterial) {
            this.baseMaterial = baseMaterial;
            return this;
        }

        public Builder renderMaterial(String renderMaterial) {
            this.renderMaterial = renderMaterial;
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

        public Builder placeable(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        public Builder breakable(boolean breakable) {
            this.breakable = breakable;
            return this;
        }

        public Builder textures(String top, String side, String bottom) {
            this.topTexture = top;
            this.sideTexture = side;
            this.bottomTexture = bottom;
            return this;
        }

        public Builder sideTextures(String side1, String side2, String side3, String side4) {
            this.side1Texture = side1;
            this.side2Texture = side2;
            this.side3Texture = side3;
            this.side4Texture = side4;
            return this;
        }

        public Builder customData(Map<String, Object> customData) {
            this.customData = customData;
            return this;
        }

        public Builder breakSettings(Map<String, Object> breakSettings) {
            this.breakSettings = breakSettings;
            return this;
        }

        public Builder interactionSettings(Map<String, Object> interactionSettings) {
            this.interactionSettings = interactionSettings;
            return this;
        }

        public Builder displaySettings(Map<String, Object> displaySettings) {
            this.displaySettings = displaySettings;
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

        public Builder animations(boolean rotate, boolean floatBob, boolean pulse) {
            this.rotate = rotate;
            this.floatBob = floatBob;
            this.pulse = pulse;
            return this;
        }

        public BlockDefinition build() {
            return new BlockDefinition(this);
        }
    }
}
