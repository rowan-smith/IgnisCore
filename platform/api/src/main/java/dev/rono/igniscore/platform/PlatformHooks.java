package dev.rono.igniscore.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.OptionalInt;

public interface PlatformHooks {

    PlatformType getPlatformType();

    void applyCustomModelData(ItemStack item, int modelData);

    OptionalInt readCustomModelData(ItemStack item);

    void applyItemMeta(ItemMeta meta, Component displayName, List<Component> lore, NamespacedKey itemModel);

    void sendResourcePack(Player player, String url, byte[] hash, boolean force);

    void sendMessage(CommandSender sender, Component message);

    boolean isBlockReplaceable(Block block);

    NamespacedKey getSoundKey(Sound sound);
}
