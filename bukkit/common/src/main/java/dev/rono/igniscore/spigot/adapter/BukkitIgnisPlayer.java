package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public final class BukkitIgnisPlayer implements IgnisPlayer {
    private final Player handle;

    public BukkitIgnisPlayer(Player handle) {
        this.handle = handle;
    }

    public Player getHandle() {
        return handle;
    }

    @Override
    public java.util.UUID getUniqueId() {
        return handle.getUniqueId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public IgnisLocation getLocation() {
        return BukkitBridge.toIgnis(handle.getLocation());
    }

    @Override
    public IgnisLocation getEyeLocation() {
        return BukkitBridge.toIgnis(handle.getEyeLocation());
    }

    @Override
    public IgnisWorld getWorld() {
        return BukkitBridge.wrap(handle.getWorld());
    }

    @Override
    public void sendMessage(String miniMessage) {
        handle.sendMessage(miniMessage);
    }

    @Override
    public void openInventory(Object nativeInventory) {
        if (nativeInventory instanceof org.bukkit.inventory.Inventory inventory) {
            handle.openInventory(inventory);
        }
    }

    @Override
    public void applyPotionEffect(String effectKey, int durationTicks, int amplifier) {
        PotionEffectType type = PotionEffectType.getByName(effectKey.toUpperCase(Locale.ROOT));
        if (type == null) {
            return;
        }
        handle.addPotionEffect(new PotionEffect(type, Math.max(1, durationTicks), Math.max(0, amplifier)));
    }
}
