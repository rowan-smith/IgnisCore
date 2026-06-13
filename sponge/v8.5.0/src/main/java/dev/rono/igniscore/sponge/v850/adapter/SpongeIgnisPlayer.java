package dev.rono.igniscore.sponge.v850.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Inventory;

public final class SpongeIgnisPlayer implements IgnisPlayer {
    private final ServerPlayer handle;

    public SpongeIgnisPlayer(ServerPlayer handle) {
        this.handle = handle;
    }

    public ServerPlayer getHandle() {
        return handle;
    }

    @Override
    public java.util.UUID getUniqueId() {
        return handle.uniqueId();
    }

    @Override
    public String getName() {
        return handle.name();
    }

    @Override
    public IgnisLocation getLocation() {
        return SpongeBridge.toIgnis(handle.serverLocation());
    }

    @Override
    public IgnisLocation getEyeLocation() {
        return getLocation();
    }

    @Override
    public IgnisWorld getWorld() {
        return SpongeBridge.wrap(handle.world());
    }

    @Override
    public void sendMessage(String miniMessage) {
        handle.sendMessage(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    @Override
    public void openInventory(Object nativeInventory) {
        if (nativeInventory instanceof Inventory inventory) {
            handle.openInventory(inventory);
        }
    }
}
