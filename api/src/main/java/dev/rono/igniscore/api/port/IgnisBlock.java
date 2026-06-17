package dev.rono.igniscore.api.port;

/**
 * Platform-neutral block handle for interaction callbacks.
 *
 * <p>Wraps a placed block in the world so extension strategies can read or
 * change its material without depending on host-specific block types.</p>
 */
public interface IgnisBlock {

    /**
     * @return the world position of this block
     */
    IgnisLocation getLocation();

    /**
     * @return the current material key (for example {@code minecraft:stone})
     */
    String getMaterialKey();

    /**
     * Replaces the block's material in the world.
     *
     * @param materialKey target material key
     */
    void setMaterialKey(String materialKey);
}
