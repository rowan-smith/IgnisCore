package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BehaviorTestSupport {
    private BehaviorTestSupport() {
    }

    public static TestContext createContext() {
        return createContext(NoopEventBus.INSTANCE);
    }

    public static TestContext createContext(dev.rono.igniscore.api.event.IgnisEventBus eventBus) {
        RecordingIgnisWorld world = new RecordingIgnisWorld();
        RecordingEffectService effects = new RecordingEffectService();
        WorldReturningExtensionSupport extensionSupport = new WorldReturningExtensionSupport(world);
        IgnisRegionService regionService = new NoopIgnisRegionService();
        IgnisIntegrationRegistry integrationRegistry = new IgnisIntegrationRegistry() {
            @Override
            public boolean isEnabled(String integrationId) {
                return "region".equals(integrationId) || "nbt".equals(integrationId)
                        || "nbt-entity".equals(integrationId);
            }

            @Override
            public String providerName(String integrationId) {
                return "test";
            }

            @Override
            public java.util.Set<String> enabledIntegrationIds() {
                return java.util.Set.of("region", "nbt", "nbt-entity");
            }

            @Override
            public void requireEnabled(String integrationId) {
            }
        };
        IgnisStrategyContext context = new IgnisStrategyContext(
                new NoopIgnisScheduler(),
                new NoopIgnisNbtService(),
                new NoopIgnisProtocolService(),
                effects,
                regionService,
                new NoopIgnisHologramService(),
                new NoopIgnisNpcService(),
                integrationRegistry,
                extensionSupport,
                eventBus);
        return new TestContext(context, world, effects);
    }

    public static RuntimeBlockInstance blockInstance(BlockDefinition definition) {
        return new RuntimeBlockInstance(
                UUID.randomUUID(),
                definition,
                new IgnisLocation("world", 1, 2, 3));
    }

    public record TestContext(
            IgnisStrategyContext context,
            RecordingIgnisWorld world,
            RecordingEffectService effects) {
    }

    public static final class RecordingIgnisWorld implements IgnisWorld {
        private final List<String> sounds = new ArrayList<>();
        private final List<ExplosionCall> explosions = new ArrayList<>();
        private final List<ParticleCall> particles = new ArrayList<>();

        @Override
        public UUID getUniqueId() {
            return UUID.randomUUID();
        }

        @Override
        public String getName() {
            return "world";
        }

        @Override
        public void createExplosion(IgnisLocation location, float power, boolean fire, boolean blockDamage) {
            explosions.add(new ExplosionCall(location, power, fire, blockDamage));
        }

        @Override
        public void playSound(IgnisLocation location, String soundKey, float volume, float pitch) {
            sounds.add(soundKey);
        }

        @Override
        public void spawnParticle(IgnisLocation location, String particleKey, int count,
                                  double offsetX, double offsetY, double offsetZ, double speed) {
            particles.add(new ParticleCall(particleKey, count));
        }

        @Override
        public Object spawnProjectile(String projectileType, IgnisLocation location, IgnisPlayer shooter,
                                      double velocityX, double velocityY, double velocityZ) {
            return new Object();
        }

        @Override
        public Object spawnEntity(String entityType, IgnisLocation location) {
            return new Object();
        }

        @Override
        public void setEntityVelocity(Object platformEntity, double velocityX, double velocityY, double velocityZ) {
        }

        @Override
        public IgnisLocation getEntityLocation(Object platformEntity) {
            return new IgnisLocation("world", 0, 0, 0);
        }

        @Override
        public boolean isEntityValid(Object platformEntity) {
            return true;
        }

        @Override
        public String getBlockMaterialKey(IgnisLocation location) {
            return "stone";
        }

        @Override
        public void setBlockMaterialKey(IgnisLocation location, String materialKey) {
        }

        @Override
        public Object spawnFallingBlock(IgnisLocation location, String materialKey) {
            return new Object();
        }

        @Override
        public java.util.Collection<Object> getNearbyEntities(IgnisLocation center, double radius) {
            return List.of();
        }

        @Override
        public List<IgnisPlayer> getPlayersNear(IgnisLocation center, double radius) {
            return List.of();
        }

        @Override
        public void setEntityTarget(Object platformEntity, IgnisPlayer target) {
        }

        @Override
        public void configurePrimedTnt(Object platformEntity, int fuseTicks, float yield, boolean incendiary) {
        }

        @Override
        public void removeEntity(Object platformEntity) {
        }

        @Override
        public void setChunkForceLoaded(IgnisLocation location, boolean forceLoaded) {
        }

        public List<String> sounds() {
            return List.copyOf(sounds);
        }

        public List<ExplosionCall> explosions() {
            return List.copyOf(explosions);
        }

        public List<ParticleCall> particles() {
            return List.copyOf(particles);
        }
    }

    public record ExplosionCall(IgnisLocation location, float power, boolean fire, boolean blockDamage) {
    }

    public record ParticleCall(String particleKey, int count) {
    }

    public static final class RecordingEffectService implements IgnisEffectService {
        private final List<String> sounds = new ArrayList<>();

        @Override
        public void playSound(IgnisLocation location, String soundKey, float volume, float pitch) {
            sounds.add(soundKey);
        }

        @Override
        public void playFakeExplosion(IgnisLocation location, float power,
                                       java.util.Collection<IgnisPlayer> players) {
        }

        @Override
        public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
        }

        public List<String> sounds() {
            return List.copyOf(sounds);
        }
    }

    private static final class WorldReturningExtensionSupport implements ExtensionSupport {
        private final InMemoryExtensionSupport delegate = new InMemoryExtensionSupport();
        private final IgnisWorld world;

        private WorldReturningExtensionSupport(IgnisWorld world) {
            this.world = world;
        }

        @Override
        public void registerDropCollector(IgnisLocation location, dev.rono.igniscore.api.collection.IgnisDropCollector collector) {
            delegate.registerDropCollector(location, collector);
        }

        @Override
        public void unregisterDropCollector(IgnisLocation location) {
            delegate.unregisterDropCollector(location);
        }

        @Override
        public void registerCustomInventory(Object nativeInventory,
                                            dev.rono.igniscore.api.inventory.IgnisCustomInventory handler) {
            delegate.registerCustomInventory(nativeInventory, handler);
        }

        @Override
        public void unregisterCustomInventory(Object nativeInventory) {
            delegate.unregisterCustomInventory(nativeInventory);
        }

        @Override
        public IgnisWorld resolveWorld(IgnisLocation location) {
            return world;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisInventory createInventory(Object holder, int size,
                                                                          net.kyori.adventure.text.Component title) {
            return delegate.createInventory(holder, size, title);
        }

        @Override
        public IgnisItem createItem(String materialKey, int amount) {
            return delegate.createItem(materialKey, amount);
        }

        @Override
        public void openInventory(IgnisPlayer player, dev.rono.igniscore.api.port.IgnisInventory inventory) {
            delegate.openInventory(player, inventory);
        }

        @Override
        public IgnisPlayer wrapPlayer(Object nativeObject) {
            return delegate.wrapPlayer(nativeObject);
        }

        @Override
        public java.nio.file.Path getDataDirectory() {
            return delegate.getDataDirectory();
        }
    }

    public static final class NoopIgnisScheduler implements dev.rono.igniscore.api.port.IgnisScheduler {
        @Override
        public IgnisTask runLater(IgnisLocation location, Runnable task, long delayTicks) {
            return cancelledTask();
        }

        @Override
        public IgnisTask runRepeating(IgnisLocation location, Runnable task, long delayTicks, long periodTicks) {
            return cancelledTask();
        }

        @Override
        public void runGlobal(Runnable task) {
        }

        @Override
        public void runGlobalLater(Runnable task, long delayTicks) {
        }

        private static IgnisTask cancelledTask() {
            return new IgnisTask() {
                @Override
                public void cancel() {
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }
            };
        }
    }

    public static final class NoopIgnisNbtService implements dev.rono.igniscore.api.service.IgnisNbtService {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public boolean supportsEntityData() {
            return true;
        }

        @Override
        public void setItemString(IgnisItem item, String key, String value) {
        }

        @Override
        public String getItemString(IgnisItem item, String key) {
            return null;
        }

        @Override
        public void setItemInt(IgnisItem item, String key, int value) {
        }

        @Override
        public int getItemInt(IgnisItem item, String key, int defaultValue) {
            return defaultValue;
        }

        @Override
        public void setItemBoolean(IgnisItem item, String key, boolean value) {
        }

        @Override
        public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
            return defaultValue;
        }

        @Override
        public void setEntityString(Object nativeEntity, String key, String value) {
        }

        @Override
        public String getEntityString(Object nativeEntity, String key) {
            return null;
        }
    }

    public static final class NoopIgnisRegionService implements IgnisRegionService {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public boolean isWorldEditBacked() {
            return false;
        }

        @Override
        public void breakHollowSphere(IgnisWorld world, IgnisLocation center, int outerRadius, int shellThickness,
                                       boolean staggered, int batchSize, int batchDelayTicks,
                                       dev.rono.igniscore.api.port.IgnisScheduler scheduler) {
        }

        @Override
        public void breakTorus(IgnisWorld world, IgnisLocation center, int majorRadius, int minorRadius,
                                boolean staggered, int batchSize, int batchDelayTicks,
                                dev.rono.igniscore.api.port.IgnisScheduler scheduler) {
        }

        @Override
        public void breakCylinderDown(IgnisWorld world, IgnisLocation center, int radius, int depth,
                                       boolean staggered, int batchSize, int batchDelayTicks,
                                       dev.rono.igniscore.api.port.IgnisScheduler scheduler) {
        }

        @Override
        public void breakUnderwater(IgnisWorld world, IgnisLocation center, int radius,
                                     boolean staggered, int batchSize, int batchDelayTicks,
                                     dev.rono.igniscore.api.port.IgnisScheduler scheduler) {
        }

        @Override
        public void breakWithPredicate(IgnisWorld world, IgnisLocation center, int radius,
                                        java.util.function.Predicate<IgnisLocation> predicate, boolean staggered,
                                        int batchSize, int batchDelayTicks,
                                        dev.rono.igniscore.api.port.IgnisScheduler scheduler,
                                        String primaryParticle, String secondaryParticle) {
        }
    }

    public static final class NoopIgnisProtocolService implements IgnisProtocolService {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public void sendFakeExplosion(IgnisLocation location, float power, java.util.Collection<IgnisPlayer> players) {
        }
    }

    public static final class NoopIgnisHologramService implements IgnisHologramService {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public Object createTextHologram(IgnisLocation location, java.util.List<String> lines) {
            return null;
        }

        @Override
        public void updateText(Object hologramHandle, java.util.List<String> lines) {
        }

        @Override
        public void delete(Object hologramHandle) {
        }

        @Override
        public void teleport(Object hologramHandle, IgnisLocation location) {
        }
    }

    public static final class NoopIgnisNpcService implements IgnisNpcService {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String providerName() {
            return "test";
        }

        @Override
        public Object spawnNpc(IgnisLocation location, String displayName) {
            return null;
        }

        @Override
        public void setTarget(Object npcHandle, IgnisPlayer target) {
        }

        @Override
        public void remove(Object npcHandle) {
        }
    }
}
