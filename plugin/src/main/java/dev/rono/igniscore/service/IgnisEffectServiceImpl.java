package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.service.IgnisEffectService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class IgnisEffectServiceImpl implements IgnisEffectService {
    private final ConfiguredEffectService configuredEffectService;
    private final VisualEffectService visualEffectService;

    @Inject
    public IgnisEffectServiceImpl(ConfiguredEffectService configuredEffectService,
                                  VisualEffectService visualEffectService) {
        this.configuredEffectService = configuredEffectService;
        this.visualEffectService = visualEffectService;
    }

    @Override
    public void playSound(Location location, String soundName, float volume, float pitch) {
        configuredEffectService.playSound(location, soundName, volume, pitch);
    }

    @Override
    public void playFakeExplosion(Location location, float power, Collection<Player> players) {
        visualEffectService.playFakeExplosion(location, power, players);
    }

    @Override
    public void showBlockPreview(Player player, Location location, Material material) {
        visualEffectService.showBlockPreview(player, location, material);
    }
}
