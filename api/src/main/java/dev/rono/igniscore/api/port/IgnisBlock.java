package dev.rono.igniscore.api.port;

/**
 * Platform-neutral block handle for interaction callbacks.
 */
public interface IgnisBlock {

    IgnisLocation getLocation();

    String getMaterialKey();

    void setMaterialKey(String materialKey);
}
