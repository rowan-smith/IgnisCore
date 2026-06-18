package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.asMapList;
import static dev.rono.igniscore.util.ConfigValueReader.getDouble;
import static dev.rono.igniscore.util.ConfigValueReader.getInt;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class SpongeConfiguredEffectService {
    private final SpongePluginContext pluginContext;
    private final PlatformAdapter platformAdapter;

    @Inject
    public SpongeConfiguredEffectService(SpongePluginContext pluginContext,
                                         PlatformAdapter platformAdapter) {
        this.pluginContext = pluginContext;
        this.platformAdapter = platformAdapter;
    }

    public void playSound(IgnisLocation location, String soundName, float volume, float pitch) {
        SoundType sound = resolveSound(soundName);
        if (sound == null) {
            pluginContext.debug("Invalid sound in block config: " + soundName);
            return;
        }

        var world = platformAdapter.resolveWorld(location);
        if (world == null) {
            return;
        }
        var spongeWorld = ((dev.rono.igniscore.sponge.adapter.SpongeIgnisWorld) world).getHandle();
        ServerLocation serverLocation = SpongeBridge.toSponge(location, spongeWorld);
        Vector3d position = serverLocation.position();
        for (var player : spongeWorld.players()) {
            player.playSound(
                    net.kyori.adventure.sound.Sound.sound(
                            sound,
                            net.kyori.adventure.sound.Sound.Source.BLOCK,
                            volume,
                            pitch),
                    position);
        }
    }

    public void spawnConfiguredParticles(IgnisLocation location,
                                         List<?> particles,
                                         String fallbackParticleKey,
                                         int fallbackCount,
                                         double fallbackOffsetX,
                                         double fallbackOffsetY,
                                         double fallbackOffsetZ,
                                         double fallbackSpeed) {
        if (particles == null || particles.isEmpty()) {
            spawnParticle(location, fallbackParticleKey, fallbackCount, fallbackOffsetX, fallbackOffsetY,
                    fallbackOffsetZ, fallbackSpeed, BlockTypes.STONE.get().defaultState());
            return;
        }

        for (Map<String, Object> map : asMapList(particles)) {
            String particleKey = getString(map, "type", fallbackParticleKey);
            int count = getInt(map, "count", fallbackCount);
            double offsetX = getDouble(map, "offset_x", fallbackOffsetX);
            double offsetY = getDouble(map, "offset_y", fallbackOffsetY);
            double offsetZ = getDouble(map, "offset_z", fallbackOffsetZ);
            double speed = getDouble(map, "speed", fallbackSpeed);
            String blockMaterial = getString(map, "block", "minecraft:stone");
            BlockState blockState = resolveBlockState(blockMaterial);
            spawnParticle(location, particleKey, count, offsetX, offsetY, offsetZ, speed, blockState);
        }
    }

    private SoundType resolveSound(String soundName) {
        if (soundName == null || soundName.isBlank()) {
            return null;
        }

        String normalized = soundName.strip();
        ResourceKey explicitKey = ResourceKey.resolve(platformAdapter.resolveSoundKey(normalized));
        var resolved = RegistryTypes.SOUND_TYPE.get().findValue(explicitKey);
        if (resolved.isPresent()) {
            return resolved.get();
        }

        return RegistryTypes.SOUND_TYPE.get().findValue(
                ResourceKey.resolve("minecraft:" + normalized.toLowerCase(Locale.ROOT).replace('_', '.')))
                .orElse(null);
    }

    private void spawnParticle(IgnisLocation location,
                               String particleKey,
                               int count,
                               double offsetX,
                               double offsetY,
                               double offsetZ,
                               double speed,
                               BlockState blockState) {
        var particleType = SpongeRegistrySupport.findParticleType(ResourceKey.resolve(
                particleKey.toLowerCase(Locale.ROOT).replace('_', '.')));
        if (particleType.isEmpty()) {
            particleType = SpongeRegistrySupport.findParticleType(ResourceKey.resolve("minecraft:block"));
        }
        if (particleType.isEmpty()) {
            pluginContext.debug("Invalid particle in block config: " + particleKey);
            return;
        }

        var world = platformAdapter.resolveWorld(location);
        if (world == null) {
            return;
        }
        var spongeWorld = ((dev.rono.igniscore.sponge.adapter.SpongeIgnisWorld) world).getHandle();
        ParticleEffect.Builder builder = ParticleEffect.builder()
                .type(particleType.get())
                .quantity(Math.max(1, count))
                .offset(Vector3d.from(offsetX, offsetY, offsetZ))
                .velocity(Vector3d.from(speed, speed, speed));
        if (particleType.get().equals(ParticleTypes.BLOCK.get())
                || particleType.get().equals(ParticleTypes.FALLING_DUST.get())) {
            builder.option(org.spongepowered.api.effect.particle.ParticleOptions.BLOCK_STATE.get(), blockState);
        }
        ParticleEffect effect = builder.build();
        for (var player : spongeWorld.players()) {
            player.spawnParticles(effect, location.x(), location.y(), location.z());
        }
    }

    private BlockState resolveBlockState(String materialKey) {
        return SpongeRegistrySupport.findBlockType(ResourceKey.resolve(materialKey.toLowerCase(Locale.ROOT)))
                .map(type -> type.defaultState())
                .orElse(BlockTypes.STONE.get().defaultState());
    }
}
