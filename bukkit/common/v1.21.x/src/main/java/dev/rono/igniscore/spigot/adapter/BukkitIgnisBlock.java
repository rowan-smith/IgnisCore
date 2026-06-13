package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;

public final class BukkitIgnisBlock implements IgnisBlock {
    private final Block handle;

    public BukkitIgnisBlock(Block handle) {
        this.handle = handle;
    }

    public Block getHandle() {
        return handle;
    }

    @Override
    public IgnisLocation getLocation() {
        return BukkitBridge.toIgnis(handle.getLocation());
    }

    @Override
    public String getMaterialKey() {
        return handle.getType().name().toLowerCase();
    }

    @Override
    public void setMaterialKey(String materialKey) {
        Material material = Material.matchMaterial(materialKey);
        if (material != null) {
            handle.setType(material);
        }
    }
}
