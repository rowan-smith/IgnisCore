package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;

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
    public void playSound(IgnisLocation location, String soundName, float volume, float pitch) {
        configuredEffectService.playSound(location, soundName, volume, pitch);
    }

    @Override
    public void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
        visualEffectService.playFakeExplosion(location, power, players);
    }

    @Override
    public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
        visualEffectService.showBlockPreview(player, location, materialKey);
    }
}
