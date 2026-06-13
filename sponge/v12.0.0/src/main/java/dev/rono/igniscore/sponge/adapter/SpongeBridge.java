package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.Locale;
import java.util.UUID;

public final class SpongeBridge {
    private SpongeBridge() {
    }

    public static IgnisLocation toIgnis(ServerLocation location) {
        ServerWorld world = location.world();
        UUID worldId = world.uniqueId();
        String worldName = world.key().asString();
        return new IgnisLocation(worldId, worldName,
                location.x(), location.y(), location.z(), 0f, 0f);
    }

    public static ServerLocation toSponge(IgnisLocation location, ServerWorld defaultWorld) {
        ServerWorld world = resolveWorld(location, defaultWorld);
        return ServerLocation.of(world, location.x(), location.y(), location.z());
    }

    public static ServerWorld resolveWorld(IgnisLocation location, ServerWorld defaultWorld) {
        if (location.worldName() != null) {
            var manager = defaultWorld.engine().game().server().worldManager();
            var resolved = manager.world(org.spongepowered.api.ResourceKey.resolve(location.worldName()));
            if (resolved.isPresent()) {
                return resolved.get();
            }
        }
        return defaultWorld;
    }

    public static SpongeIgnisItem wrap(ItemStack item) {
        return item == null || item.isEmpty() ? null : new SpongeIgnisItem(item);
    }

    public static ItemStack unwrap(IgnisItem item) {
        if (item == null) {
            return null;
        }
        if (item instanceof SpongeIgnisItem spongeItem) {
            return spongeItem.getHandle();
        }
        Object nativeItem = item.nativeItem();
        if (nativeItem instanceof ItemStack stack) {
            return stack;
        }
        throw new IllegalArgumentException("Unsupported IgnisItem implementation: " + item.getClass().getName());
    }

    public static SpongeIgnisPlayer wrap(ServerPlayer player) {
        return player == null ? null : new SpongeIgnisPlayer(player);
    }

    public static ServerPlayer unwrap(IgnisPlayer player) {
        if (player == null) {
            return null;
        }
        if (player instanceof SpongeIgnisPlayer spongePlayer) {
            return spongePlayer.getHandle();
        }
        throw new IllegalArgumentException("Unsupported IgnisPlayer implementation: " + player.getClass().getName());
    }

    public static SpongeIgnisWorld wrap(ServerWorld world) {
        return world == null ? null : new SpongeIgnisWorld(world);
    }

    public static SpongeIgnisBlock wrap(BlockSnapshot block) {
        return block == null ? null : new SpongeIgnisBlock(block);
    }

    public static String materialKey(org.spongepowered.api.item.ItemType itemType) {
        return materialKey(itemType.key(RegistryTypes.ITEM_TYPE));
    }

    public static String materialKey(org.spongepowered.api.block.BlockType blockType) {
        return materialKey(blockType.key(RegistryTypes.BLOCK_TYPE));
    }

    public static String materialKey(org.spongepowered.api.ResourceKey key) {
        return key.asString().toLowerCase(Locale.ROOT);
    }

    public static IgnisInteraction toIgnisInteraction(InteractItemEvent event) {
        if (event instanceof InteractItemEvent.Secondary) {
            return IgnisInteraction.RIGHT_CLICK_AIR;
        }
        if (event instanceof InteractItemEvent.Primary) {
            return IgnisInteraction.LEFT_CLICK_AIR;
        }
        return IgnisInteraction.RIGHT_CLICK_AIR;
    }

    public static IgnisInteraction toIgnisInteraction(InteractBlockEvent event) {
        if (event instanceof InteractBlockEvent.Secondary) {
            return IgnisInteraction.RIGHT_CLICK_BLOCK;
        }
        if (event instanceof InteractBlockEvent.Primary) {
            return IgnisInteraction.LEFT_CLICK_BLOCK;
        }
        return IgnisInteraction.RIGHT_CLICK_BLOCK;
    }
}
