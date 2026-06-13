package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class BukkitIgnisWorld implements IgnisWorld {
    private final org.bukkit.World handle;

    public BukkitIgnisWorld(org.bukkit.World handle) {
        this.handle = handle;
    }

    public org.bukkit.World getHandle() {
        return handle;
    }

    @Override
    public java.util.UUID getUniqueId() {
        return handle.getUID();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public void createExplosion(IgnisLocation location, float power, boolean fire, boolean blockDamage) {
        handle.createExplosion(BukkitBridge.toBukkit(location, handle), power, fire, blockDamage);
    }

    @Override
    public void playSound(IgnisLocation location, String soundKey, float volume, float pitch) {
        Location bukkitLocation = BukkitBridge.toBukkit(location, handle);
        try {
            Sound sound = Sound.valueOf(soundKey.toUpperCase().replace('.', '_'));
            handle.playSound(bukkitLocation, sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            handle.playSound(bukkitLocation, soundKey.toLowerCase(Locale.ROOT), volume, pitch);
        }
    }

    @Override
    public void spawnParticle(IgnisLocation location, String particleKey, int count,
                              double offsetX, double offsetY, double offsetZ, double speed) {
        Location bukkitLocation = BukkitBridge.toBukkit(location, handle);
        try {
            Particle particle = Particle.valueOf(particleKey.toUpperCase(Locale.ROOT));
            handle.spawnParticle(particle, bukkitLocation, count, offsetX, offsetY, offsetZ, speed);
        } catch (IllegalArgumentException ignored) {
            // Unknown particle on this version
        }
    }

    @Override
    public Object spawnProjectile(String projectileType, IgnisLocation location, IgnisPlayer shooter,
                                  double velocityX, double velocityY, double velocityZ) {
        Location spawn = BukkitBridge.toBukkit(location, handle);
        if ("snowball".equalsIgnoreCase(projectileType)) {
            Player nativeShooter = BukkitBridge.unwrap(shooter);
            Snowball snowball = handle.spawn(spawn, Snowball.class, entity -> entity.setShooter(nativeShooter));
            snowball.setVelocity(new Vector(velocityX, velocityY, velocityZ));
            return snowball;
        }
        throw new UnsupportedOperationException("Unsupported projectile type: " + projectileType);
    }

    @Override
    public Object spawnEntity(String entityType, IgnisLocation location) {
        Location spawn = BukkitBridge.toBukkit(location, handle);
        EntityType type = EntityType.valueOf(entityType.toUpperCase(Locale.ROOT));
        return handle.spawnEntity(spawn, type);
    }

    @Override
    public void setEntityVelocity(Object platformEntity, double velocityX, double velocityY, double velocityZ) {
        if (platformEntity instanceof Entity entity) {
            entity.setVelocity(new Vector(velocityX, velocityY, velocityZ));
        }
    }

    @Override
    public IgnisLocation getEntityLocation(Object platformEntity) {
        if (platformEntity instanceof Entity entity) {
            return BukkitBridge.toIgnis(entity.getLocation());
        }
        return null;
    }

    @Override
    public boolean isEntityValid(Object platformEntity) {
        return platformEntity instanceof Entity entity && entity.isValid();
    }

    @Override
    public String getBlockMaterialKey(IgnisLocation location) {
        Block block = BukkitBridge.toBukkit(location, handle).getBlock();
        return block.getType().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public void setBlockMaterialKey(IgnisLocation location, String materialKey) {
        Block block = BukkitBridge.toBukkit(location, handle).getBlock();
        Material material = Material.matchMaterial(materialKey);
        if (material != null) {
            block.setType(material);
        }
    }

    @Override
    public Object spawnFallingBlock(IgnisLocation location, String materialKey) {
        Location spawn = BukkitBridge.toBukkit(location, handle);
        Material material = Material.matchMaterial(materialKey);
        if (material == null) {
            return null;
        }
        FallingBlock fallingBlock = handle.spawnFallingBlock(spawn, material.createBlockData());
        fallingBlock.setDropItem(false);
        return fallingBlock;
    }

    @Override
    public Collection<Object> getNearbyEntities(IgnisLocation center, double radius) {
        Location location = BukkitBridge.toBukkit(center, handle);
        List<Object> entities = new ArrayList<>();
        for (Entity entity : handle.getNearbyEntities(location, radius, radius, radius)) {
            entities.add(entity);
        }
        return entities;
    }

    @Override
    public List<IgnisPlayer> getPlayersNear(IgnisLocation center, double radius) {
        Location location = BukkitBridge.toBukkit(center, handle);
        List<IgnisPlayer> players = new ArrayList<>();
        for (Player player : handle.getPlayers()) {
            if (player.getLocation().distance(location) < radius) {
                players.add(BukkitBridge.wrap(player));
            }
        }
        return players;
    }

    @Override
    public void setEntityTarget(Object platformEntity, IgnisPlayer target) {
        if (platformEntity instanceof Mob mob) {
            mob.setTarget(BukkitBridge.unwrap(target));
        }
    }

    @Override
    public void configurePrimedTnt(Object platformEntity, int fuseTicks, float yield, boolean incendiary) {
        if (platformEntity instanceof TNTPrimed tnt) {
            tnt.setFuseTicks(Math.max(1, fuseTicks));
            tnt.setYield(yield);
            tnt.setIsIncendiary(incendiary);
        }
    }

    @Override
    public void removeEntity(Object platformEntity) {
        if (platformEntity instanceof Entity entity) {
            entity.remove();
        }
    }
}
