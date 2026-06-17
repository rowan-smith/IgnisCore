package dev.rono.igniscore.api.port;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Platform-neutral world handle for world-scoped operations.
 *
 * <p>Covers explosions, sounds, particles, entity lifecycle, block material
 * changes, and nearby player queries. Entity parameters use opaque
 * {@link Object} handles returned by spawn methods.</p>
 */
public interface IgnisWorld {

    /**
     * @return stable unique id for this world
     */
    UUID getUniqueId();

    /**
     * @return human-readable world name
     */
    String getName();

    /**
     * Creates an explosion at the given location.
     *
     * @param location center of the explosion
     * @param power yield strength (vanilla TNT uses 4.0)
     * @param fire whether the explosion sets blocks on fire
     * @param blockDamage whether the explosion breaks blocks
     */
    void createExplosion(IgnisLocation location, float power, boolean fire, boolean blockDamage);

    /**
     * Plays a sound at the given location for nearby players.
     *
     * @param location sound origin
     * @param soundKey namespaced sound key
     * @param volume volume multiplier
     * @param pitch pitch multiplier
     */
    void playSound(IgnisLocation location, String soundKey, float volume, float pitch);

    /**
     * Spawns particles at the given location.
     *
     * @param location particle origin
     * @param particleKey namespaced particle key
     * @param count number of particles
     * @param offsetX random spread on the x axis
     * @param offsetY random spread on the y axis
     * @param offsetZ random spread on the z axis
     * @param speed particle speed or extra data depending on type
     */
    void spawnParticle(IgnisLocation location, String particleKey, int count,
                       double offsetX, double offsetY, double offsetZ, double speed);

    /**
     * Spawns a projectile entity with an initial velocity.
     *
     * @param projectileType entity type key (for example {@code minecraft:arrow})
     * @param location spawn position and orientation
     * @param shooter player credited as shooter, or {@code null}
     * @param velocityX initial velocity on the x axis
     * @param velocityY initial velocity on the y axis
     * @param velocityZ initial velocity on the z axis
     * @return opaque platform entity handle (for example Bukkit {@code Entity})
     */
    Object spawnProjectile(String projectileType, IgnisLocation location, IgnisPlayer shooter,
                           double velocityX, double velocityY, double velocityZ);

    /**
     * Spawns a generic entity at the given location.
     *
     * @param entityType entity type key
     * @param location spawn position and orientation
     * @return opaque platform entity handle
     */
    Object spawnEntity(String entityType, IgnisLocation location);

    /**
     * Sets the velocity of an existing entity.
     *
     * @param platformEntity opaque entity from a spawn method
     * @param velocityX velocity on the x axis
     * @param velocityY velocity on the y axis
     * @param velocityZ velocity on the z axis
     */
    void setEntityVelocity(Object platformEntity, double velocityX, double velocityY, double velocityZ);

    /**
     * @param platformEntity opaque entity handle
     * @return current position of the entity
     */
    IgnisLocation getEntityLocation(Object platformEntity);

    /**
     * @param platformEntity opaque entity handle
     * @return whether the entity still exists in the world
     */
    boolean isEntityValid(Object platformEntity);

    /**
     * @param location block position to query
     * @return material key at the position
     */
    String getBlockMaterialKey(IgnisLocation location);

    /**
     * Replaces the block at the given position.
     *
     * @param location block position
     * @param materialKey target material key
     */
    void setBlockMaterialKey(IgnisLocation location, String materialKey);

    /**
     * Spawns a falling block entity (gravity-affected block).
     *
     * @param location spawn position
     * @param materialKey material the falling block represents
     * @return opaque platform entity handle
     */
    Object spawnFallingBlock(IgnisLocation location, String materialKey);

    /**
     * @param center search center
     * @param radius spherical search radius
     * @return opaque platform entities within range
     */
    Collection<Object> getNearbyEntities(IgnisLocation center, double radius);

    /**
     * @param center search center
     * @param radius spherical search radius
     * @return players within range, wrapped as {@link IgnisPlayer}
     */
    List<IgnisPlayer> getPlayersNear(IgnisLocation center, double radius);

    /**
     * Sets the attack or follow target of a mob entity.
     *
     * @param platformEntity opaque mob entity handle
     * @param target player to target
     */
    void setEntityTarget(Object platformEntity, IgnisPlayer target);

    /**
     * Configures primed TNT entity fuse, yield, and incendiary flag.
     *
     * @param platformEntity opaque primed TNT entity handle
     * @param fuseTicks ticks until detonation
     * @param yield explosion power
     * @param incendiary whether the blast sets fire
     */
    void configurePrimedTnt(Object platformEntity, int fuseTicks, float yield, boolean incendiary);

    /**
     * Removes an entity from the world.
     *
     * @param platformEntity opaque entity handle to remove
     */
    void removeEntity(Object platformEntity);
}
