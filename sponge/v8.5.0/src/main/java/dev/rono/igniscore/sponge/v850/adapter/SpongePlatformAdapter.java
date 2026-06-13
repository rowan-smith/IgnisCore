package dev.rono.igniscore.sponge.v850.adapter;

import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.sponge.v850.IgnisSpongePlugin;
import dev.rono.igniscore.sponge.v850.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.v850.listener.SpongeItemListener;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class SpongePlatformAdapter implements PlatformAdapter {
    private final IgnisSpongePlugin plugin;
    private final PluginContainer container;
    private final Game game;
    private final EventManager eventManager;
    private final SpongeIgnisScheduler scheduler;
    private final Path dataDirectory;
    private final Logger logger;

    public SpongePlatformAdapter(IgnisSpongePlugin plugin,
                                 PluginContainer container,
                                 Game game,
                                 EventManager eventManager) {
        this.plugin = plugin;
        this.container = container;
        this.game = game;
        this.eventManager = eventManager;
        this.scheduler = new SpongeIgnisScheduler(game.server().scheduler(), container);
        this.dataDirectory = game.gameDirectory().resolve("plugins").resolve("IgnisCore");
        this.logger = Logger.getLogger("IgnisCore");
    }

    public IgnisSpongePlugin plugin() {
        return plugin;
    }

    public PluginContainer container() {
        return container;
    }

    public Game game() {
        return game;
    }

    public Server server() {
        return game.server();
    }

    public EventManager eventManager() {
        return eventManager;
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.SPONGE;
    }

    @Override
    public String getMinecraftVersion() {
        return game.platform().minecraftVersion().name();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Path getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public IgnisScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public IgnisItem wrapItem(Object nativeItem) {
        return nativeItem instanceof ItemStack stack ? SpongeBridge.wrap(stack) : null;
    }

    @Override
    public IgnisPlayer wrapPlayer(Object nativePlayer) {
        return nativePlayer instanceof ServerPlayer player ? SpongeBridge.wrap(player) : null;
    }

    @Override
    public IgnisBlock wrapBlock(Object nativeBlock) {
        return nativeBlock instanceof BlockSnapshot snapshot ? SpongeBridge.wrap(snapshot) : null;
    }

    @Override
    public IgnisWorld wrapWorld(Object nativeWorld) {
        return nativeWorld instanceof org.spongepowered.api.world.server.ServerWorld world
                ? SpongeBridge.wrap(world) : null;
    }

    @Override
    public IgnisLocation unwrapLocation(Object nativeLocation) {
        return nativeLocation instanceof org.spongepowered.api.world.server.ServerLocation location
                ? SpongeBridge.toIgnis(location) : null;
    }

    @Override
    public Object nativeLocation(IgnisLocation location) {
        var defaultWorld = server().worldManager().worlds().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No worlds loaded"));
        return SpongeBridge.toSponge(location, defaultWorld);
    }

    @Override
    public void applyCustomModelData(Object nativeItem, int modelData) {
        if (nativeItem instanceof ItemStack item) {
            item.offer(Keys.CUSTOM_MODEL_DATA, modelData);
        }
    }

    @Override
    public OptionalInt readCustomModelData(Object nativeItem) {
        if (nativeItem instanceof ItemStack item) {
            return item.get(Keys.CUSTOM_MODEL_DATA).map(OptionalInt::of).orElse(OptionalInt.empty());
        }
        return OptionalInt.empty();
    }

    @Override
    public void applyItemMeta(Object nativeItem, Component displayName, List<Component> lore, String itemModelKey) {
        if (!(nativeItem instanceof ItemStack item)) {
            return;
        }
        if (displayName != null) {
            item.offer(Keys.CUSTOM_NAME, displayName);
        }
        if (lore != null && !lore.isEmpty()) {
            item.offer(Keys.LORE, lore);
        }
    }

    @Override
    public void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force) {
        // Resource pack delivery is optional on Sponge; left as a no-op in the minimal runtime.
    }

    @Override
    public void sendMessage(Object nativeSender, Component message) {
        if (nativeSender instanceof net.kyori.adventure.audience.Audience audience) {
            audience.sendMessage(message);
        }
    }

    @Override
    public boolean isBlockReplaceable(Object nativeBlock) {
        if (nativeBlock instanceof BlockSnapshot snapshot) {
            return snapshot.state().type().isAnyOf(BlockTypes.AIR.get());
        }
        return false;
    }

    @Override
    public String resolveSoundKey(String bukkitStyleSoundName) {
        return bukkitStyleSoundName.toLowerCase(Locale.ROOT).replace('_', '.');
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        Inventory inventory = Inventory.builder()
                .slots(size)
                .build();
        return new SpongeIgnisInventory(inventory);
    }

    @Override
    public void registerEventListeners(Object listenerRegistry) {
        if (listenerRegistry instanceof Iterable<?> listeners) {
            for (Object listener : listeners) {
                registerListener(listener);
            }
            return;
        }
        registerListener(listenerRegistry);
    }

    private void registerListener(Object listener) {
        if (listener != null) {
            eventManager.registerListeners(container, listener);
        }
    }

    @Override
    public void registerCommand(String name, Object commandExecutor) {
        // Commands are registered from IgnisSpongePlugin during RegisterCommandEvent.
    }

    @Override
    public IgnisWorld resolveWorld(IgnisLocation location) {
        if (location == null || location.worldName() == null) {
            return server().worldManager().worlds().stream()
                    .findFirst()
                    .map(SpongeBridge::wrap)
                    .orElse(null);
        }
        return server().worldManager().world(org.spongepowered.api.ResourceKey.resolve(location.worldName()))
                .or(() -> server().worldManager().worlds().stream().findFirst())
                .map(SpongeBridge::wrap)
                .orElse(null);
    }

    @Override
    public IgnisItem createMaterialItem(String materialKey, int amount) {
        org.spongepowered.api.ResourceKey key = org.spongepowered.api.ResourceKey.resolve(materialKey.toLowerCase(Locale.ROOT));
        var itemType = dev.rono.igniscore.sponge.v850.support.SpongeRegistrySupport.findItemType(key)
                .orElse(org.spongepowered.api.item.ItemTypes.STONE.get());
        return SpongeBridge.wrap(ItemStack.of(itemType, amount));
    }

    @Override
    public void clearBlock(IgnisLocation location) {
        IgnisWorld world = resolveWorld(location);
        if (world != null) {
            world.setBlockMaterialKey(dev.rono.igniscore.api.util.Locations.toBlock(location), "minecraft:air");
        }
    }

    @Override
    public void shutdown() {
        Scheduler scheduler = game.server().scheduler();
        scheduler.tasks(container).forEach(task -> task.cancel());
    }
}
