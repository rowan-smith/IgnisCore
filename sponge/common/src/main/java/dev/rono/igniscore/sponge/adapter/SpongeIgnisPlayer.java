package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.Ticks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Override
    public void applyPotionEffect(String effectKey, int durationTicks, int amplifier) {
        ResourceKey key = ResourceKey.resolve("minecraft:" + effectKey.toLowerCase(Locale.ROOT));
        RegistryTypes.POTION_EFFECT_TYPE.get().findValue(key).ifPresent(type -> {
            PotionEffect effect = PotionEffect.builder()
                    .potionType(type)
                    .duration(Ticks.of(Math.max(1, durationTicks)))
                    .amplifier(Math.max(0, amplifier))
                    .build();
            List<PotionEffect> effects = new ArrayList<>(handle.get(Keys.POTION_EFFECTS).orElse(List.of()));
            effects.add(effect);
            handle.offer(Keys.POTION_EFFECTS, effects);
        });
    }
}
