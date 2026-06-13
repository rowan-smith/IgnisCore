package dev.rono.igniscore.api.port;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Platform-neutral world handle for world-scoped operations.
 */
public interface IgnisWorld {

    UUID getUniqueId();

    String getName();

    void createExplosion(IgnisLocation location, float power, boolean fire, boolean blockDamage);

    void playSound(IgnisLocation location, String soundKey, float volume, float pitch);

    void spawnParticle(IgnisLocation location, String particleKey, int count,
                       double offsetX, double offsetY, double offsetZ, double speed);

    /**
     * @return opaque platform entity handle (e.g. Bukkit Entity, Sponge Entity)
     */
    Object spawnProjectile(String projectileType, IgnisLocation location, IgnisPlayer shooter,
                           double velocityX, double velocityY, double velocityZ);

    Object spawnEntity(String entityType, IgnisLocation location);

    void setEntityVelocity(Object platformEntity, double velocityX, double velocityY, double velocityZ);

    IgnisLocation getEntityLocation(Object platformEntity);

    boolean isEntityValid(Object platformEntity);

    String getBlockMaterialKey(IgnisLocation location);

    void setBlockMaterialKey(IgnisLocation location, String materialKey);

    Object spawnFallingBlock(IgnisLocation location, String materialKey);

    Collection<Object> getNearbyEntities(IgnisLocation center, double radius);

    List<IgnisPlayer> getPlayersNear(IgnisLocation center, double radius);

    void setEntityTarget(Object platformEntity, IgnisPlayer target);

    void configurePrimedTnt(Object platformEntity, int fuseTicks, float yield, boolean incendiary);

    void removeEntity(Object platformEntity);
}
