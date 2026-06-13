package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.api.port.IgnisLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getDouble;
import static dev.rono.igniscore.util.ConfigValueReader.getInt;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class ConfiguredEffectService {
    private final Plugin plugin;
    private final PlatformHooks platformHooks;

    @Inject
    public ConfiguredEffectService(Main plugin, PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.platformHooks = platformHooks;
    }

    ConfiguredEffectService(Plugin plugin, PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.platformHooks = platformHooks;
    }

    public void playSound(IgnisLocation location, String soundName, float volume, float pitch) {
        playSound(BukkitBridge.toBukkit(location), soundName, volume, pitch);
    }

    public void playSound(Location location, String soundName, float volume, float pitch) {
        Sound sound = resolveSound(soundName, platformHooks);
        if (sound == null) {
            debug("Invalid sound in block config: " + soundName);
            return;
        }
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void spawnConfiguredParticles(IgnisLocation location, List<?> particles, Particle fallbackParticle, int fallbackCount,
                                         double fallbackOffsetX, double fallbackOffsetY, double fallbackOffsetZ,
                                         double fallbackSpeed) {
        spawnConfiguredParticles(BukkitBridge.toBukkit(location), particles, fallbackParticle, fallbackCount,
                fallbackOffsetX, fallbackOffsetY, fallbackOffsetZ, fallbackSpeed);
    }

    public void spawnConfiguredParticles(Location location, List<?> particles, Particle fallbackParticle, int fallbackCount,
                                         double fallbackOffsetX, double fallbackOffsetY, double fallbackOffsetZ,
                                         double fallbackSpeed) {
        if (particles.isEmpty()) {
            spawnParticle(location, fallbackParticle, fallbackCount, fallbackOffsetX, fallbackOffsetY, fallbackOffsetZ,
                    fallbackSpeed, Material.STONE);
            return;
        }

        for (Object particleConfig : particles) {
            if (!(particleConfig instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            Particle particle;
            try {
                particle = Particle.valueOf(getString(map, "type", fallbackParticle.name()).toUpperCase());
            } catch (IllegalArgumentException ignored) {
                debug("Invalid particle in block config: " + map.get("type"));
                continue;
            }

            int count = getInt(map, "count", fallbackCount);
            double offsetX = getDouble(map, "offset_x", fallbackOffsetX);
            double offsetY = getDouble(map, "offset_y", fallbackOffsetY);
            double offsetZ = getDouble(map, "offset_z", fallbackOffsetZ);
            double speed = getDouble(map, "speed", fallbackSpeed);
            Material blockMaterial = Material.matchMaterial(getString(map, "block", "STONE"));
            spawnParticle(location, particle, count, offsetX, offsetY, offsetZ, speed,
                    blockMaterial != null ? blockMaterial : Material.STONE);
        }
    }

    private static Sound resolveSound(String soundName, PlatformHooks platformHooks) {
        if (soundName == null || soundName.isBlank()) {
            return null;
        }

        String normalized = soundName.strip();
        NamespacedKey explicitKey = NamespacedKey.fromString(normalized.toLowerCase(Locale.ROOT));
        if (explicitKey != null) {
            Sound sound = Registry.SOUNDS.get(explicitKey);
            if (sound != null) {
                return sound;
            }
        }

        String enumStyle = normalized.toUpperCase(Locale.ROOT);
        for (Sound sound : Registry.SOUNDS) {
            NamespacedKey key = platformHooks.getSoundKey(sound);
            if (key != null && toEnumStyle(key).equals(enumStyle)) {
                return sound;
            }
        }

        return Registry.SOUNDS.get(NamespacedKey.minecraft(normalized.toLowerCase(Locale.ROOT).replace('_', '.')));
    }

    private static String toEnumStyle(NamespacedKey key) {
        return key.getKey().toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private void debug(String message) {
        if (plugin instanceof Main main) {
            main.debug(message);
        }
    }

    private void spawnParticle(Location location, Particle particle, int count, double offsetX, double offsetY,
                               double offsetZ, double speed, Material blockMaterial) {
        if (particle == Particle.BLOCK || particle == Particle.BLOCK_CRUMBLE || particle == Particle.FALLING_DUST) {
            location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed,
                    blockMaterial.createBlockData());
        } else {
            location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
        }
    }
}
