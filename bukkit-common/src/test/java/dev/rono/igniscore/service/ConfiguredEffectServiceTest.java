package dev.rono.igniscore.service;

import dev.rono.igniscore.support.MockBukkitTestBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ConfiguredEffectServiceTest extends MockBukkitTestBase {
    private ConfiguredEffectService effectService;

    @BeforeEach
    void setUpService() {
        effectService = new ConfiguredEffectService(plugin, platformHooks);
    }

    @Test
    void playsConfiguredSoundWithoutThrowing() {
        Location location = new Location(world, 0, 64, 0);
        effectService.playSound(location, "BLOCK_STONE_BREAK", 1.0f, 1.0f);
        effectService.playSound(location, "NOT_A_REAL_SOUND", 1.0f, 1.0f);
    }

    @Test
    void spawnsConfiguredAndFallbackParticlesWithoutThrowing() {
        Location location = new Location(world, 1, 64, 1);

        effectService.spawnConfiguredParticles(location, List.of(), Particle.CRIT, 3, 0.1, 0.1, 0.1, 0.01);
        effectService.spawnConfiguredParticles(location, List.of(
                Map.of("type", "FLAME", "count", 5, "offset_x", 0.2, "offset_y", 0.2, "offset_z", 0.2, "speed", 0.01),
                Map.of("type", "BLOCK", "block", "STONE", "count", 4)
        ), Particle.SMOKE, 1, 0.0, 0.0, 0.0, 0.0);
        effectService.spawnConfiguredParticles(location, List.of(
                Map.of("type", "NOT_A_REAL_PARTICLE")
        ), Particle.SMOKE, 1, 0.0, 0.0, 0.0, 0.0);
    }
}
