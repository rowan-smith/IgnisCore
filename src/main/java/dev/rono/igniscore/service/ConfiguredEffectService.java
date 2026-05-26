package dev.rono.igniscore.service;

import dev.rono.igniscore.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;
import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getDouble;
import static dev.rono.igniscore.util.ConfigValueReader.getInt;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class ConfiguredEffectService {
    private final Main plugin;

    public ConfiguredEffectService(Main plugin) {
        this.plugin = plugin;
    }

    public void playSound(Location location, String soundName, float volume, float pitch) {
        try {
            location.getWorld().playSound(location, Sound.valueOf(soundName.toUpperCase()), volume, pitch);
        } catch (IllegalArgumentException ignored) {
            plugin.debug("Invalid sound in block config: " + soundName);
        }
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
                plugin.debug("Invalid particle in block config: " + map.get("type"));
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
