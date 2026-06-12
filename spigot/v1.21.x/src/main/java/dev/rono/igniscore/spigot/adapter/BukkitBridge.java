package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import dev.rono.igniscore.api.port.IgnisInteraction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class BukkitBridge {
    private BukkitBridge() {
    }

    public static IgnisLocation toIgnis(Location location) {
        World world = location.getWorld();
        UUID worldId = world != null ? world.getUID() : null;
        String worldName = world != null ? world.getName() : "world";
        return new IgnisLocation(worldId, worldName, location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    public static Location toBukkit(IgnisLocation location) {
        World world = resolveWorld(location);
        return new Location(world, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
    }

    public static Location toBukkit(IgnisLocation location, World defaultWorld) {
        World world = location.worldName() != null ? Bukkit.getWorld(location.worldName()) : null;
        if (world == null) {
            world = defaultWorld;
        }
        if (world == null && location.worldId() != null) {
            for (World candidate : Bukkit.getWorlds()) {
                if (candidate.getUID().equals(location.worldId())) {
                    world = candidate;
                    break;
                }
            }
        }
        if (world == null) {
            world = Bukkit.getWorlds().getFirst();
        }
        return new Location(world, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
    }

    private static World resolveWorld(IgnisLocation location) {
        if (location.worldName() != null) {
            World byName = Bukkit.getWorld(location.worldName());
            if (byName != null) {
                return byName;
            }
        }
        if (location.worldId() != null) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getUID().equals(location.worldId())) {
                    return world;
                }
            }
        }
        return Bukkit.getWorlds().getFirst();
    }

    public static BukkitIgnisItem wrap(ItemStack item) {
        return item == null ? null : new BukkitIgnisItem(item);
    }

    public static ItemStack unwrap(IgnisItem item) {
        if (item == null) {
            return null;
        }
        if (item instanceof BukkitIgnisItem bukkitItem) {
            return bukkitItem.getHandle();
        }
        Object nativeItem = item.nativeItem();
        if (nativeItem instanceof ItemStack stack) {
            return stack;
        }
        throw new IllegalArgumentException("Unsupported IgnisItem implementation: " + item.getClass().getName());
    }

    public static BukkitIgnisPlayer wrap(Player player) {
        return player == null ? null : new BukkitIgnisPlayer(player);
    }

    public static Player unwrap(IgnisPlayer player) {
        if (player == null) {
            return null;
        }
        if (player instanceof BukkitIgnisPlayer bukkitPlayer) {
            return bukkitPlayer.getHandle();
        }
        throw new IllegalArgumentException("Unsupported IgnisPlayer implementation: " + player.getClass().getName());
    }

    public static BukkitIgnisWorld wrap(World world) {
        return world == null ? null : new BukkitIgnisWorld(world);
    }

    public static BukkitIgnisBlock wrap(Block block) {
        return block == null ? null : new BukkitIgnisBlock(block);
    }

    public static IgnisInteraction toIgnisInteraction(Action action) {
        if (action == null) {
            return IgnisInteraction.RIGHT_CLICK_AIR;
        }
        return switch (action) {
            case RIGHT_CLICK_AIR -> IgnisInteraction.RIGHT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK -> IgnisInteraction.RIGHT_CLICK_BLOCK;
            case LEFT_CLICK_AIR -> IgnisInteraction.LEFT_CLICK_AIR;
            case LEFT_CLICK_BLOCK -> IgnisInteraction.LEFT_CLICK_BLOCK;
            case PHYSICAL -> IgnisInteraction.PHYSICAL;
        };
    }
}
