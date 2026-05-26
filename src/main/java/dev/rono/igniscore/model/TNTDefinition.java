package dev.rono.igniscore.model;

import net.kyori.adventure.text.Component;
import java.util.List;

public class TNTDefinition {
    private final String id;
    private final Component title;
    private final List<Component> description;
    private final int fuse;
    private final double power;
    private final double radius;
    private final double multiplier;
    private final boolean fire;
    private final boolean blockDamage;
    private final boolean screenShake;
    private final String topTexture;
    private final String sideTexture;
    private final String bottomTexture;
    private final String explosionType;
    private final int customModelData;
    
    // Entity Payload
    private final String entityPayloadType;
    private final int entityPayloadCount;
    private final String entityPayloadBehavior;
    private final boolean entityPayloadTargetPlayers;

    public TNTDefinition(String id, Component title, List<Component> description, int fuse, double power, 
                         double radius, double multiplier, boolean fire, boolean blockDamage, 
                         boolean screenShake, String topTexture, String sideTexture, 
                         String bottomTexture, String explosionType, int customModelData,
                         String entityPayloadType, int entityPayloadCount,
                         String entityPayloadBehavior, boolean entityPayloadTargetPlayers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fuse = fuse;
        this.power = power;
        this.radius = radius;
        this.multiplier = multiplier;
        this.fire = fire;
        this.blockDamage = blockDamage;
        this.screenShake = screenShake;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.explosionType = explosionType;
        this.customModelData = customModelData;
        this.entityPayloadType = entityPayloadType;
        this.entityPayloadCount = entityPayloadCount;
        this.entityPayloadBehavior = entityPayloadBehavior;
        this.entityPayloadTargetPlayers = entityPayloadTargetPlayers;
    }

    public String getId() { return id; }
    public Component getTitle() { return title; }
    public List<Component> getDescription() { return description; }
    public int getFuse() { return fuse; }
    public double getPower() { return power; }
    public double getRadius() { return radius; }
    public double getMultiplier() { return multiplier; }
    public boolean isFire() { return fire; }
    public boolean isBlockDamage() { return blockDamage; }
    public boolean isScreenShake() { return screenShake; }
    public String getTopTexture() { return topTexture; }
    public String getSideTexture() { return sideTexture; }
    public String getBottomTexture() { return bottomTexture; }
    public String getExplosionType() { return explosionType; }
    public int getCustomModelData() { return customModelData; }
    public String getEntityPayloadType() { return entityPayloadType; }
    public int getEntityPayloadCount() { return entityPayloadCount; }
    public String getEntityPayloadBehavior() { return entityPayloadBehavior; }
    public boolean isEntityPayloadTargetPlayers() { return entityPayloadTargetPlayers; }
}
