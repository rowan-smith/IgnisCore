package dev.rono.igniscore.platform.spigot;

import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.platform.PlatformType;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public final class SpigotPlatformHooks implements PlatformHooks {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final BukkitAudiences audiences;

    public SpigotPlatformHooks(JavaPlugin plugin) {
        this.audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.SPIGOT;
    }

    @Override
    public void applyCustomModelData(ItemStack item, int modelData) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.setCustomModelData(modelData);
        item.setItemMeta(meta);
    }

    @Override
    public OptionalInt readCustomModelData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(meta.getCustomModelData());
    }

    @Override
    public void applyItemMeta(ItemMeta meta, Component displayName, List<Component> lore, NamespacedKey itemModel) {
        if (displayName != null) {
            meta.setDisplayName(LEGACY.serialize(displayName));
        }
        if (lore != null) {
            meta.setLore(lore.stream().map(LEGACY::serialize).collect(Collectors.toList()));
        }
        if (itemModel != null) {
            meta.setItemModel(itemModel);
        }
    }

    @Override
    public void sendResourcePack(Player player, String url, byte[] hash, boolean force) {
        if (hash != null) {
            player.setResourcePack(url, hash);
            return;
        }
        player.setResourcePack(url);
    }

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        if (message == null) {
            return;
        }
        audiences.sender(sender).sendMessage(message);
    }

    @Override
    public boolean isBlockReplaceable(Block block) {
        return block.getType().isAir() || !block.getType().isSolid();
    }

    @Override
    public NamespacedKey getSoundKey(Sound sound) {
        String enumName = sound.name().toLowerCase().replace('_', '.');
        return NamespacedKey.minecraft(enumName);
    }

    @Override
    public void shutdown() {
        audiences.close();
    }
}
