package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisNbtService;

/**
 * Cooldown tracking for consumable items via NBT timestamps.
 */
public final class ConsumableSupport {
    private ConsumableSupport() {
    }

    public static boolean isOnCooldown(IgnisNbtService nbt, IgnisItem item, String key, long cooldownTicks) {
        if (nbt == null) {
            return false;
        }
        long last = nbt.getItemInt(item, key, 0);
        return last > 0 && System.currentTimeMillis() - last < cooldownTicks * 50L;
    }

    public static void markUsed(IgnisNbtService nbt, IgnisItem item, String key) {
        if (nbt == null) {
            return;
        }
        nbt.setItemInt(item, key, (int) System.currentTimeMillis());
    }

    public static void consumeOne(IgnisItem item) {
        item.setAmount(Math.max(0, item.getAmount() - 1));
    }

    public static void notifyCooldown(IgnisPlayer player, long remainingMs) {
        player.sendActionBar("<gray>Cooldown: " + (remainingMs / 1000) + "s</gray>");
    }
}
