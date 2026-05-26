package dev.rono.igniscore.model;

import net.kyori.adventure.text.Component;
import java.util.List;
import java.util.Map;

public class BlockDefinition {
    private final String id;
    private final Component title;
    private final List<Component> description;
    
    private final boolean placeable;
    private final boolean breakable;
    
    private final String topTexture;
    private final String sideTexture;
    private final String bottomTexture;
    
    private final String strategy;
    private final int fuse;
    private final double radius;
    private final Map<String, Object> customData;
    
    private final int customModelData;
    
    // Animations
    private final boolean rotate;
    private final boolean floatBob;
    private final boolean pulse;

    public BlockDefinition(String id, Component title, List<Component> description, 
                           boolean placeable, boolean breakable,
                           String topTexture, String sideTexture, String bottomTexture, 
                           String strategy, int fuse, double radius, Map<String, Object> customData, 
                           int customModelData, boolean rotate, boolean floatBob, boolean pulse) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.placeable = placeable;
        this.breakable = breakable;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.strategy = strategy;
        this.fuse = fuse;
        this.radius = radius;
        this.customData = customData;
        this.customModelData = customModelData;
        this.rotate = rotate;
        this.floatBob = floatBob;
        this.pulse = pulse;
    }

    public String getId() { return id; }
    public Component getTitle() { return title; }
    public List<Component> getDescription() { return description; }
    public boolean isPlaceable() { return placeable; }
    public boolean isBreakable() { return breakable; }
    public String getTopTexture() { return topTexture; }
    public String getSideTexture() { return sideTexture; }
    public String getBottomTexture() { return bottomTexture; }
    public String getStrategy() { return strategy; }
    public int getFuse() { return fuse; }
    public double getRadius() { return radius; }
    public Map<String, Object> getCustomData() { return customData; }
    public int getCustomModelData() { return customModelData; }
    public boolean isRotate() { return rotate; }
    public boolean isFloatBob() { return floatBob; }
    public boolean isPulse() { return pulse; }
}
