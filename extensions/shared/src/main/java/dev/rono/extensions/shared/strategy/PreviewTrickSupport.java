package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisProtocolService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fake explosions, block previews, and hologram countdown helpers.
 */
public final class PreviewTrickSupport {
    private PreviewTrickSupport() {
    }

    public static void playDecoyExplosions(IgnisEffectService effects,
                                            IgnisWorld world,
                                            IgnisLocation realCenter,
                                            int decoyCount,
                                            double spread,
                                            float power) {
        for (int i = 0; i < decoyCount; i++) {
            double angle = (Math.PI * 2 * i) / decoyCount;
            IgnisLocation decoy = realCenter.add(Math.cos(angle) * spread, 0, Math.sin(angle) * spread);
            effects.playFakeExplosion(decoy, power, world.getPlayersNear(decoy, 48));
            world.spawnParticle(decoy, "EXPLOSION", 2, 0.2, 0.2, 0.2, 0.01);
        }
    }

    public static void scareExplosion(IgnisEffectService effects,
                                       IgnisProtocolService protocol,
                                       IgnisWorld world,
                                       IgnisLocation center,
                                       double radius,
                                       float fakePower) {
        var players = world.getPlayersNear(center, radius);
        effects.playFakeExplosion(center, fakePower, players);
        if (protocol != null && protocol.isEnabled()) {
            protocol.sendFakeExplosion(center, fakePower, players);
        }
        for (IgnisPlayer player : players) {
            player.getWorld().playSound(player.getLocation(), "ENTITY_GENERIC_EXPLODE", 4.0f, 0.4f);
            player.getWorld().playSound(player.getLocation(), "ENTITY_LIGHTNING_BOLT_THUNDER", 3.0f, 0.3f);
        }
        world.spawnParticle(center, "EXPLOSION_EMITTER", 8, 2, 1, 2, 0);
    }

    public static void silentDetonation(IgnisWorld world,
                                         IgnisLocation center,
                                         float realPower,
                                         boolean blockDamage) {
        world.createExplosion(center, realPower, false, blockDamage);
    }

    public static void ringBlockPreviews(IgnisEffectService effects,
                                          IgnisPlayer viewer,
                                          IgnisLocation center,
                                          int count,
                                          double radius,
                                          String materialKey) {
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            IgnisLocation preview = center.add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            effects.showBlockPreview(viewer, preview, materialKey);
        }
    }

    public static void cycleBlockPreviews(IgnisEffectService effects,
                                           List<IgnisPlayer> viewers,
                                           IgnisLocation center,
                                           int radius,
                                           String[] materials,
                                           int tick) {
        String material = materials[tick % materials.length];
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                IgnisLocation loc = center.add(x, 0, z);
                for (IgnisPlayer viewer : viewers) {
                    effects.showBlockPreview(viewer, loc, material);
                }
            }
        }
    }

    public static Object spawnCountdownHologram(IgnisHologramService holograms,
                                                 IgnisLocation location,
                                                 int seconds) {
        if (holograms == null || !holograms.isEnabled()) {
            return null;
        }
        List<String> lines = List.of("§c⚠ " + seconds + "s");
        return holograms.createTextHologram(location.add(0, 1.2, 0), lines);
    }

    public static void updateCountdownHologram(IgnisHologramService holograms,
                                                Object handle,
                                                int secondsRemaining) {
        if (holograms == null || !holograms.isEnabled() || handle == null) {
            return;
        }
        holograms.updateText(handle, List.of("§c⚠ " + secondsRemaining + "s"));
    }

    public static void deleteHologram(IgnisHologramService holograms, Object handle) {
        if (holograms != null && handle != null) {
            holograms.delete(handle);
        }
    }

    public static void delayedFakeBursts(IgnisEffectService effects,
                                          IgnisWorld world,
                                          IgnisLocation center,
                                          int bursts,
                                          int delayTicks,
                                          IgnisScheduler scheduler) {
        int[] count = {0};
        IgnisTask[] ref = {null};
        ref[0] = scheduler.runRepeating(center, () -> {
            if (count[0]++ >= bursts) {
                if (ref[0] != null) {
                    ref[0].cancel();
                }
                return;
            }
            effects.playFakeExplosion(center, 5.0f, world.getPlayersNear(center, 32));
            world.spawnParticle(center, "EXPLOSION", 2, 0.5, 0.5, 0.5, 0.01);
        }, delayTicks, delayTicks);
    }

    public static void forNearbyPlayers(IgnisWorld world, IgnisLocation center, double radius, Consumer<IgnisPlayer> action) {
        for (IgnisPlayer player : world.getPlayersNear(center, radius)) {
            action.accept(player);
        }
    }

    public static List<IgnisLocation> circleOffsets(int count, double radius) {
        List<IgnisLocation> offsets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            offsets.add(new IgnisLocation(null, null, Math.cos(angle) * radius, 0, Math.sin(angle) * radius, 0f, 0f));
        }
        return offsets;
    }
}
