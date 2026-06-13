package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.platform.PlatformHookLoader;
import dev.rono.igniscore.platform.PlatformHooks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class BukkitPlatformAdapter implements PlatformAdapter {
    private final JavaPlugin plugin;
    private final IgnisScheduler scheduler;
    private final PlatformHooks platformHooks;

    public BukkitPlatformAdapter(JavaPlugin plugin) {
        this(plugin, PlatformHookLoader.load(plugin));
    }

    public BukkitPlatformAdapter(JavaPlugin plugin, PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.scheduler = BukkitSchedulerFactory.create(plugin);
        this.platformHooks = platformHooks;
    }

    public PlatformHooks legacyHooks() {
        return platformHooks;
    }

    @Override
    public PlatformType getPlatformType() {
        return switch (platformHooks.getPlatformType()) {
            case PAPER -> PlatformType.PAPER;
            case SPIGOT -> PlatformType.SPIGOT;
        };
    }

    @Override
    public String getMinecraftVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public Path getDataDirectory() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public IgnisScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public IgnisItem wrapItem(Object nativeItem) {
        return nativeItem instanceof ItemStack stack ? BukkitBridge.wrap(stack) : null;
    }

    @Override
    public IgnisPlayer wrapPlayer(Object nativePlayer) {
        return nativePlayer instanceof Player player ? BukkitBridge.wrap(player) : null;
    }

    @Override
    public IgnisBlock wrapBlock(Object nativeBlock) {
        return nativeBlock instanceof Block block ? BukkitBridge.wrap(block) : null;
    }

    @Override
    public IgnisWorld wrapWorld(Object nativeWorld) {
        return nativeWorld instanceof org.bukkit.World world ? BukkitBridge.wrap(world) : null;
    }

    @Override
    public IgnisLocation unwrapLocation(Object nativeLocation) {
        return nativeLocation instanceof Location location ? BukkitBridge.toIgnis(location) : null;
    }

    @Override
    public Object nativeLocation(IgnisLocation location) {
        return BukkitBridge.toBukkit(location);
    }

    @Override
    public void applyCustomModelData(Object nativeItem, int modelData) {
        if (nativeItem instanceof ItemStack item) {
            platformHooks.applyCustomModelData(item, modelData);
        }
    }

    @Override
    public OptionalInt readCustomModelData(Object nativeItem) {
        if (nativeItem instanceof ItemStack item) {
            return platformHooks.readCustomModelData(item);
        }
        return OptionalInt.empty();
    }

    @Override
    public void applyItemMeta(Object nativeItem, Component displayName, List<Component> lore, String itemModelKey) {
        if (!(nativeItem instanceof ItemStack item)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        NamespacedKey itemModel = itemModelKey != null
                ? NamespacedKey.fromString(itemModelKey.toLowerCase(Locale.ROOT))
                : null;
        platformHooks.applyItemMeta(meta, displayName, lore, itemModel);
        item.setItemMeta(meta);
    }

    @Override
    public void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force) {
        if (nativePlayer instanceof Player player) {
            platformHooks.sendResourcePack(player, url, hash, force);
        }
    }

    @Override
    public void sendMessage(Object nativeSender, Component message) {
        if (nativeSender instanceof CommandSender sender) {
            platformHooks.sendMessage(sender, message);
        }
    }

    @Override
    public boolean isBlockReplaceable(Object nativeBlock) {
        return nativeBlock instanceof Block block && platformHooks.isBlockReplaceable(block);
    }

    @Override
    public String resolveSoundKey(String bukkitStyleSoundName) {
        return bukkitStyleSoundName.toLowerCase(Locale.ROOT).replace('_', '.');
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        Inventory inventory = Bukkit.createInventory(
                holder instanceof org.bukkit.inventory.InventoryHolder inventoryHolder ? inventoryHolder : null,
                size,
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(title));
        return new BukkitIgnisInventory(inventory);
    }

    @Override
    public void registerEventListeners(Object listenerRegistry) {
        if (listenerRegistry instanceof Iterable<?> listeners) {
            for (Object listener : listeners) {
                if (listener instanceof org.bukkit.event.Listener bukkitListener) {
                    plugin.getServer().getPluginManager().registerEvents(bukkitListener, plugin);
                }
            }
        } else if (listenerRegistry instanceof org.bukkit.event.Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void registerCommand(String name, Object commandExecutor) {
        if (plugin.getCommand(name) != null && commandExecutor instanceof org.bukkit.command.CommandExecutor executor) {
            plugin.getCommand(name).setExecutor(executor);
        }
    }

    @Override
    public IgnisWorld resolveWorld(IgnisLocation location) {
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        return bukkitLocation.getWorld() != null ? BukkitBridge.wrap(bukkitLocation.getWorld()) : null;
    }

    @Override
    public IgnisItem createMaterialItem(String materialKey, int amount) {
        org.bukkit.Material material = org.bukkit.Material.matchMaterial(materialKey);
        if (material == null) {
            material = org.bukkit.Material.STONE;
        }
        return BukkitBridge.wrap(new ItemStack(material, amount));
    }

    @Override
    public void clearBlock(IgnisLocation location) {
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        Block block = bukkitLocation.getBlock();
        if (block.getType() != org.bukkit.Material.AIR) {
            block.setType(org.bukkit.Material.AIR);
        }
    }

    @Override
    public void shutdown() {
        platformHooks.shutdown();
    }
}
