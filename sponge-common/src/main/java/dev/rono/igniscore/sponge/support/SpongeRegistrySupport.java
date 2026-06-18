package dev.rono.igniscore.sponge.support;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Locale;
import java.util.Optional;

public final class SpongeRegistrySupport {
    private SpongeRegistrySupport() {
    }

    public static Optional<ItemType> findItemType(ResourceKey key) {
        return RegistryTypes.ITEM_TYPE.get().findValue(key);
    }

    public static Optional<BlockType> findBlockType(ResourceKey key) {
        return RegistryTypes.BLOCK_TYPE.get().findValue(key);
    }

    public static Optional<EntityType<?>> findEntityType(ResourceKey key) {
        return RegistryTypes.ENTITY_TYPE.get().findValue(key);
    }

    public static Optional<ParticleType> findParticleType(ResourceKey key) {
        Optional<ParticleType> direct = RegistryTypes.PARTICLE_TYPE.get().findValue(key);
        if (direct.isPresent()) {
            return direct;
        }
        String raw = key.asString().toLowerCase(Locale.ROOT);
        if (!raw.contains(":")) {
            return RegistryTypes.PARTICLE_TYPE.get().findValue(ResourceKey.resolve("minecraft:" + raw));
        }
        return Optional.empty();
    }
}
