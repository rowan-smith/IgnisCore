package dev.rono.igniscore.api.model;

import net.kyori.adventure.text.Component;
import java.util.List;
import java.util.Map;

public class BlockDefinition {
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
    
    private final String strategy;
    private final int fuse;
    private final double radius;
    private final Map<String, Object> customData;
    private final Map<String, Object> breakSettings;
    private final Map<String, Object> interactionSettings;
    private final Map<String, Object> displaySettings;
    
    private final int customModelData;
    private final String extensionId;

    // Animations
    private final boolean rotate;
    private final boolean floatBob;
    private final boolean pulse;

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description, 
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture, 
                           String strategy, int fuse, double radius, Map<String, Object> customData, 
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, strategy, fuse, radius, customData, breakSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, "builtin", null, null, null, null);
    }

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           String strategy, int fuse, double radius, Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId) {
        this(id, baseMaterial, renderMaterial, title, description, placeable, breakable, topTexture, sideTexture,
                bottomTexture, strategy, fuse, radius, customData, breakSettings, interactionSettings, displaySettings,
                customModelData, rotate, floatBob, pulse, extensionId, null, null, null, null);
    }

    public BlockDefinition(String id, String baseMaterial, String renderMaterial, Component title, List<Component> description,
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture,
                           String strategy, int fuse, double radius, Map<String, Object> customData,
                           Map<String, Object> breakSettings, Map<String, Object> interactionSettings,
                           Map<String, Object> displaySettings, int customModelData, boolean rotate, boolean floatBob, boolean pulse,
                           String extensionId, String side1Texture, String side2Texture, String side3Texture, String side4Texture) {
        this.id = id;
        this.baseMaterial = baseMaterial;
        this.renderMaterial = renderMaterial;
        this.title = title;
        this.description = description;
        this.placeable = placeable;
        this.breakable = breakable;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.side1Texture = side1Texture;
        this.side2Texture = side2Texture;
        this.side3Texture = side3Texture;
        this.side4Texture = side4Texture;
        this.strategy = strategy;
        this.fuse = fuse;
        this.radius = radius;
        this.customData = customData;
        this.breakSettings = breakSettings;
        this.interactionSettings = interactionSettings;
        this.displaySettings = displaySettings;
        this.customModelData = customModelData;
        this.extensionId = extensionId != null ? extensionId : "builtin";
        this.rotate = rotate;
        this.floatBob = floatBob;
        this.pulse = pulse;
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
    public String getStrategy() { return strategy; }
    public int getFuse() { return fuse; }
    public double getRadius() { return radius; }
    public Map<String, Object> getCustomData() { return customData; }
    public Map<String, Object> getBreakSettings() { return breakSettings; }
    public Map<String, Object> getInteractionSettings() { return interactionSettings; }
    public Map<String, Object> getDisplaySettings() { return displaySettings; }
    public int getCustomModelData() { return customModelData; }
    public String getExtensionId() { return extensionId; }
    public boolean isRotate() { return rotate; }
    public boolean isFloatBob() { return floatBob; }
    public boolean isPulse() { return pulse; }
}
