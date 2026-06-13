package dev.rono.igniscore.platform.paper;

import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.platform.PlatformType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.OptionalInt;

public final class PaperPlatformHooks implements PlatformHooks {

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.PAPER;
    }

    @Override
    public void applyCustomModelData(ItemStack item, int modelData) {
        item.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addFloat((float) modelData).build()
        );
    }

    @Override
    public OptionalInt readCustomModelData(ItemStack item) {
        CustomModelData data = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (data == null || data.floats().isEmpty()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(data.floats().getFirst().intValue());
    }

    @Override
    public void applyItemMeta(ItemMeta meta, Component displayName, List<Component> lore, NamespacedKey itemModel) {
        meta.displayName(displayName);
        meta.lore(lore);
        if (itemModel != null) {
            meta.setItemModel(itemModel);
        }
    }

    @Override
    public void sendResourcePack(Player player, String url, byte[] hash, boolean force) {
        if (hash != null) {
            player.setResourcePack(url, hash, (Component) null, force);
            return;
        }
        player.setResourcePack(url, (byte[]) null, (Component) null, force);
    }

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        if (message == null) {
            return;
        }
        sender.sendMessage(message);
    }

    @Override
    public boolean isBlockReplaceable(Block block) {
        return block.isReplaceable();
    }

    @Override
    public NamespacedKey getSoundKey(Sound sound) {
        return Registry.SOUNDS.getKey(sound);
    }
}
