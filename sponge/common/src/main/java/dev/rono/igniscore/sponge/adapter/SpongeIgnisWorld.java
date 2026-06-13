package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SpongeIgnisWorld implements IgnisWorld {
    private final ServerWorld handle;

    public SpongeIgnisWorld(ServerWorld handle) {
        this.handle = handle;
    }

    public ServerWorld getHandle() {
        return handle;
    }

    @Override
    public java.util.UUID getUniqueId() {
        return handle.uniqueId();
    }

    @Override
    public String getName() {
        return handle.key().asString();
    }

    @Override
    public void createExplosion(IgnisLocation location, float power, boolean fire, boolean blockDamage) {
        ServerLocation serverLocation = SpongeBridge.toSponge(location, handle);
        Explosion explosion = Explosion.builder()
                .canCauseFire(fire)
                .shouldBreakBlocks(blockDamage)
                .radius(power)
                .location(serverLocation)
                .build();
        handle.triggerExplosion(explosion);
    }

    @Override
    public void playSound(IgnisLocation location, String soundKey, float volume, float pitch) {
        // Sound playback is implementation-specific; minimal runtime leaves this as a no-op.
    }

    @Override
    public void spawnParticle(IgnisLocation location, String particleKey, int count,
                              double offsetX, double offsetY, double offsetZ, double speed) {
    }

    @Override
    public Object spawnProjectile(String projectileType, IgnisLocation location, IgnisPlayer shooter,
                                  double velocityX, double velocityY, double velocityZ) {
        if (!"snowball".equalsIgnoreCase(projectileType)) {
            throw new UnsupportedOperationException("Unsupported projectile type: " + projectileType);
        }
        ServerLocation spawn = SpongeBridge.toSponge(location, handle);
        Entity snowball = spawn.createEntity(EntityTypes.SNOWBALL.get());
        spawn.spawnEntity(snowball);
        return snowball;
    }

    @Override
    public Object spawnEntity(String entityType, IgnisLocation location) {
        ServerLocation spawn = SpongeBridge.toSponge(location, handle);
        ResourceKey key = ResourceKey.resolve(entityType.toLowerCase());
        return SpongeRegistrySupport.findEntityType(key)
                .map(type -> {
                    Entity entity = spawn.createEntity(type);
                    spawn.spawnEntity(entity);
                    return entity;
                })
                .orElse(null);
    }

    @Override
    public void setEntityVelocity(Object platformEntity, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public IgnisLocation getEntityLocation(Object platformEntity) {
        if (platformEntity instanceof Entity entity) {
            return SpongeBridge.toIgnis(entity.serverLocation());
        }
        return null;
    }

    @Override
    public boolean isEntityValid(Object platformEntity) {
        return platformEntity instanceof Entity;
    }

    @Override
    public String getBlockMaterialKey(IgnisLocation location) {
        ServerLocation serverLocation = SpongeBridge.toSponge(location, handle);
        return SpongeBridge.materialKey(serverLocation.createSnapshot().state().type());
    }

    @Override
    public void setBlockMaterialKey(IgnisLocation location, String materialKey) {
        ServerLocation serverLocation = SpongeBridge.toSponge(location, handle);
        ResourceKey key = ResourceKey.resolve(materialKey.toLowerCase());
        SpongeRegistrySupport.findBlockType(key)
                .ifPresent(type -> serverLocation.restoreSnapshot(
                        org.spongepowered.api.block.BlockSnapshot.builder()
                                .from(serverLocation)
                                .blockState(type.defaultState())
                                .build(),
                        false,
                        BlockChangeFlags.ALL));
    }

    @Override
    public Object spawnFallingBlock(IgnisLocation location, String materialKey) {
        return null;
    }

    @Override
    public Collection<Object> getNearbyEntities(IgnisLocation center, double radius) {
        return List.of();
    }

    @Override
    public List<IgnisPlayer> getPlayersNear(IgnisLocation center, double radius) {
        ServerLocation serverLocation = SpongeBridge.toSponge(center, handle);
        List<IgnisPlayer> players = new ArrayList<>();
        for (var player : handle.players()) {
            if (player.serverLocation().position().distance(serverLocation.position()) <= radius) {
                players.add(SpongeBridge.wrap(player));
            }
        }
        return players;
    }

    @Override
    public void setEntityTarget(Object platformEntity, IgnisPlayer target) {
    }

    @Override
    public void configurePrimedTnt(Object platformEntity, int fuseTicks, float yield, boolean incendiary) {
    }

    @Override
    public void removeEntity(Object platformEntity) {
        if (platformEntity instanceof Entity entity) {
            entity.remove();
        }
    }
}
