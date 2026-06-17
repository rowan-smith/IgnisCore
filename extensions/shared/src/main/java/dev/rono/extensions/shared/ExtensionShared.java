package dev.rono.extensions.shared;

import dev.rono.extensions.shared.config.ExplosionConfig;
import dev.rono.extensions.shared.config.ExtensionConfigs;
import dev.rono.extensions.shared.config.ThrowableItemConfig;
import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.gui.SecureTradeRegistry;
import dev.rono.extensions.shared.strategy.BlockLinkSupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.ConsumableSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.LinkItemSupport;
import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Entry point for optional extension helper APIs shipped with IgnisCore.
 *
 * <p>Prefer {@code ExtensionShared.explosion()}, {@code ExtensionShared.config()}, and the other
 * grouped accessors over importing {@code *Support} classes directly. Helpers are provided at
 * runtime by the bootstrap plugin — depend on {@code dev.rono.extensions:shared} with
 * {@code provided} scope and do not shade this module into extension JARs.</p>
 *
 * @see dev.rono.igniscore.api.strategy.IgnisStrategies
 */
public final class ExtensionShared {
    private ExtensionShared() {
    }

    /** Explosion tuning and detonation helpers. */
    public static Explosion explosion() {
        return Explosion.INSTANCE;
    }

    /** Typed {@code custom_data} views for common extension kinds. */
    public static Config config() {
        return Config.INSTANCE;
    }

    /** Particles, sounds, and scan visuals. */
    public static Theatrics theatrics() {
        return Theatrics.INSTANCE;
    }

    /** Repeating tasks for placed blocks. */
    public static Ticks ticks() {
        return Ticks.INSTANCE;
    }

    /** World scanning helpers (ores, crops, copper, moss). */
    public static Scan scan() {
        return Scan.INSTANCE;
    }

    /** Item ↔ block NBT linking. */
    public static Link link() {
        return Link.INSTANCE;
    }

    /** Remote activation registry for linked blocks. */
    public static Remote remote() {
        return Remote.INSTANCE;
    }

    /** Consumable cooldown and consume-one helpers. */
    public static Consumable consumable() {
        return Consumable.INSTANCE;
    }

    /** Entity queries and radius effects. */
    public static Entities entities() {
        return Entities.INSTANCE;
    }

    /** Processing GUI inventory helpers. */
    public static Processing processing() {
        return Processing.INSTANCE;
    }

    /** Specialized explosive blast patterns. */
    public static Variants variants() {
        return Variants.INSTANCE;
    }

    /** Block GUI registries (storage, trade). */
    public static Gui gui() {
        return Gui.INSTANCE;
    }

    public static final class Explosion {
        static final Explosion INSTANCE = new Explosion();

        private Explosion() {
        }

        public int fuse(BlockDefinition definition, int defaultFuse) {
            return ExplosionSupport.fuse(definition, defaultFuse);
        }

        public double radius(BlockDefinition definition, double defaultRadius) {
            return ExplosionSupport.radius(definition, defaultRadius);
        }

        public float resolvePower(BlockDefinition definition, double defaultPower) {
            return ExplosionSupport.resolvePower(definition, defaultPower);
        }

        public float resolvePower(ItemDefinition definition, double defaultPower) {
            return ExplosionSupport.resolvePower(definition, defaultPower);
        }

        public float resolvePower(Map<String, Object> customData, double defaultPower) {
            return ExplosionSupport.resolvePower(customData, defaultPower);
        }

        public void create(IgnisWorld world, IgnisLocation location, BlockDefinition definition,
                           double defaultPower, boolean defaultFire) {
            ExplosionSupport.createExplosion(world, location, definition, defaultPower, defaultFire);
        }

        public void create(IgnisWorld world, IgnisLocation location, ItemDefinition definition,
                           double defaultPower, boolean defaultFire) {
            ExplosionSupport.createExplosion(world, location, definition, defaultPower, defaultFire);
        }

        public void create(IgnisWorld world, IgnisLocation location, Map<String, Object> customData,
                           double defaultPower, boolean defaultFire) {
            ExplosionSupport.createExplosion(world, location, customData, defaultPower, defaultFire);
        }

        public void create(IgnisWorld world, IgnisLocation location, float power, boolean fire, boolean blockDamage) {
            ExplosionSupport.createExplosion(world, location, power, fire, blockDamage);
        }

        public int fuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
            return ExplosionSupport.fuseTicks(instance, defaultFuse);
        }

        public int elapsedFuseTicks(RuntimeBlockInstance instance, int defaultFuse) {
            return ExplosionSupport.elapsedFuseTicks(instance, defaultFuse);
        }
    }

    public static final class Config {
        static final Config INSTANCE = new Config();

        private Config() {
        }

        public ExplosionConfig explosion(BlockDefinition definition) {
            return ExtensionConfigs.explosion(definition);
        }

        public ThrowableItemConfig throwable(ItemDefinition definition) {
            return ExtensionConfigs.throwable(definition);
        }
    }

    public static final class Theatrics {
        static final Theatrics INSTANCE = new Theatrics();

        private Theatrics() {
        }

        public void sparkle(IgnisWorld world, IgnisLocation center, String particle, int count) {
            TheatricsSupport.sparkle(world, center, particle, count);
        }

        public void chime(IgnisWorld world, IgnisLocation center, float pitch) {
            TheatricsSupport.chime(world, center, pitch);
        }

        public void pulseRing(IgnisWorld world, IgnisLocation center, double radius, String particle) {
            TheatricsSupport.pulseRing(world, center, radius, particle);
        }

        public void scanBeam(IgnisWorld world, IgnisLocation from, IgnisLocation toward, String particle) {
            TheatricsSupport.scanBeam(world, from, toward, particle);
        }
    }

    public static final class Ticks {
        static final Ticks INSTANCE = new Ticks();

        private Ticks() {
        }

        public void start(IgnisStrategyContext context, IgnisLocation location, long periodTicks, Runnable action) {
            PlacedTickSupport.start(context, location, periodTicks, action);
        }

        public void stop(IgnisLocation location) {
            PlacedTickSupport.stop(location);
        }
    }

    public static final class Scan {
        static final Scan INSTANCE = new Scan();

        private Scan() {
        }

        public IgnisLocation findNearestMatching(IgnisWorld world, IgnisLocation center, int radius, Set<String> targets) {
            return BlockScanSupport.findNearestMatching(world, center, radius, targets);
        }

        public IgnisLocation findNearestOre(IgnisWorld world, IgnisLocation center, int radius) {
            return BlockScanSupport.findNearestOre(world, center, radius);
        }

        public int countCrops(IgnisWorld world, IgnisLocation center, int radius) {
            return BlockScanSupport.countCrops(world, center, radius);
        }

        public void bonemealRadius(IgnisWorld world, IgnisLocation center, int radius) {
            BlockScanSupport.bonemealRadius(world, center, radius);
        }

        public void mossifyNearWater(IgnisWorld world, IgnisLocation center, int radius) {
            BlockScanSupport.mossifyNearWater(world, center, radius);
        }

        public void deoxidizeCopper(IgnisWorld world, IgnisLocation center, int radius) {
            BlockScanSupport.deoxidizeCopper(world, center, radius);
        }
    }

    public static final class Link {
        static final Link INSTANCE = new Link();

        private Link() {
        }

        public void itemOnUse(IgnisStrategyContext context,
                              IgnisPlayer player,
                              ItemDefinition definition,
                              IgnisItem item,
                              IgnisBlock clickedBlock,
                              String expectedBlockType,
                              String remoteAction,
                              double linkRange) {
            LinkItemSupport.onUse(context, player, definition, item, clickedBlock,
                    expectedBlockType, remoteAction, linkRange);
        }

        public void store(IgnisNbtService nbt, IgnisItem item, String blockTypeId, IgnisLocation location) {
            BlockLinkSupport.link(nbt, item, blockTypeId, location);
        }

        public boolean hasLink(IgnisNbtService nbt, IgnisItem item) {
            return BlockLinkSupport.hasLink(nbt, item);
        }

        public IgnisLocation readLocation(IgnisNbtService nbt, IgnisItem item) {
            return BlockLinkSupport.readLocation(nbt, item);
        }

        public String readType(IgnisNbtService nbt, IgnisItem item) {
            return BlockLinkSupport.readType(nbt, item);
        }

        public boolean withinRange(IgnisPlayer player, IgnisLocation target, double maxRange) {
            return BlockLinkSupport.withinRange(player, target, maxRange);
        }

        public void clear(IgnisNbtService nbt, IgnisItem item) {
            BlockLinkSupport.clear(nbt, item);
        }
    }

    public static final class Remote {
        static final Remote INSTANCE = new Remote();

        private Remote() {
        }

        public String key(IgnisLocation location) {
            return LinkedBlockRegistry.key(location);
        }

        public void register(IgnisLocation location, BiConsumer<IgnisPlayer, String> remoteAction) {
            LinkedBlockRegistry.register(location, remoteAction);
        }

        public void unregister(IgnisLocation location) {
            LinkedBlockRegistry.unregister(location);
        }

        public boolean activate(IgnisLocation location, IgnisPlayer player, String action) {
            return LinkedBlockRegistry.activate(location, player, action);
        }
    }

    public static final class Consumable {
        static final Consumable INSTANCE = new Consumable();

        private Consumable() {
        }

        public boolean isOnCooldown(IgnisNbtService nbt, IgnisItem item, String key, long cooldownTicks) {
            return ConsumableSupport.isOnCooldown(nbt, item, key, cooldownTicks);
        }

        public void markUsed(IgnisNbtService nbt, IgnisItem item, String key) {
            ConsumableSupport.markUsed(nbt, item, key);
        }

        public void consumeOne(IgnisItem item) {
            ConsumableSupport.consumeOne(item);
        }

        public void notifyCooldown(IgnisPlayer player, long remainingMs) {
            ConsumableSupport.notifyCooldown(player, remainingMs);
        }
    }

    public static final class Entities {
        static final Entities INSTANCE = new Entities();

        private Entities() {
        }

        public boolean isHostile(Object entity) {
            return EntityUtilSupport.isHostile(entity);
        }

        public boolean isPassive(Object entity) {
            return EntityUtilSupport.isPassive(entity);
        }

        public boolean isLootEntity(Object entity) {
            return EntityUtilSupport.isLootEntity(entity);
        }

        public void pullLoot(IgnisWorld world, IgnisLocation target, double radius, double strength) {
            EntityUtilSupport.pullLoot(world, target, radius, strength);
        }

        public void freezeInRadius(IgnisWorld world, IgnisLocation center, double radius) {
            EntityUtilSupport.freezeInRadius(world, center, radius);
        }

        public void teleportRandomHorizontal(IgnisWorld world, IgnisLocation center, double radius, double distance) {
            EntityUtilSupport.teleportRandomHorizontal(world, center, radius, distance);
        }

        public int countHostiles(IgnisWorld world, IgnisLocation center, double radius) {
            return EntityUtilSupport.countHostiles(world, center, radius);
        }

        public int countPassives(IgnisWorld world, IgnisLocation center, double radius) {
            return EntityUtilSupport.countPassives(world, center, radius);
        }

        public void herdPassives(IgnisWorld world, IgnisLocation target, double radius) {
            EntityUtilSupport.herdPassives(world, target, radius);
        }

        public void swapNearestPlayers(IgnisWorld world, IgnisLocation center, double radius) {
            EntityUtilSupport.swapNearestPlayers(world, center, radius);
        }
    }

    public static final class Processing {
        static final Processing INSTANCE = new Processing();

        private Processing() {
        }

        public boolean matches(IgnisItem item, String... materials) {
            return ProcessingGuiSupport.matches(item, materials);
        }

        public void consumeOne(IgnisInventory inventory, int slot) {
            ProcessingGuiSupport.consumeOne(inventory, slot);
        }

        public void setOutput(ExtensionSupport extensionSupport, IgnisInventory inventory, int slot,
                              String material, int amount) {
            ProcessingGuiSupport.setOutput(extensionSupport, inventory, slot, material, amount);
        }
    }

    public static final class Variants {
        static final Variants INSTANCE = new Variants();

        private Variants() {
        }

        public void cardinalSplit(IgnisWorld world, IgnisLocation center, float power, double offset) {
            ExplosionVariantsSupport.cardinalSplit(world, center, power, offset);
        }

        public void ricochetRay(IgnisWorld world, IgnisLocation start, float yaw, int bounces,
                                double step, float power) {
            ExplosionVariantsSupport.ricochetRay(world, start, yaw, bounces, step, power);
        }

        public void mirrorBlast(IgnisWorld world, IgnisLocation center, float power, double mirrorY) {
            ExplosionVariantsSupport.mirrorBlast(world, center, power, mirrorY);
        }

        public void phaseBurst(IgnisWorld world, IgnisLocation center, float power, double radius) {
            ExplosionVariantsSupport.phaseBurst(world, center, power, radius);
        }

        public float resolveYaw(IgnisWorld world, IgnisLocation block, Object triggerContext,
                                IgnisStrategyContext context) {
            return ExplosionVariantsSupport.resolveYaw(world, block, triggerContext, context);
        }
    }

    public static final class Gui {
        static final Gui INSTANCE = new Gui();

        private Gui() {
        }

        public BlockStorageRegistry blockStorage(IgnisStrategyContext context, String namespace) {
            return new BlockStorageRegistry(context, namespace);
        }

        public SecureTradeRegistry secureTrade(IgnisStrategyContext context) {
            return new SecureTradeRegistry(context);
        }
    }
}
