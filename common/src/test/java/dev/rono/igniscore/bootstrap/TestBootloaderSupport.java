package dev.rono.igniscore.bootstrap;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;

final class TestBootloaderSupport {
    static final PlatformAdapter ADAPTER = new PlatformAdapter() {
        @Override
        public PlatformType getPlatformType() {
            return PlatformType.SPIGOT;
        }

        @Override
        public String getMinecraftVersion() {
            return "test";
        }

        @Override
        public java.util.logging.Logger getLogger() {
            return java.util.logging.Logger.getLogger("test");
        }

        @Override
        public java.nio.file.Path getDataDirectory() {
            return java.nio.file.Path.of(".");
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisScheduler getScheduler() {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisItem wrapItem(Object nativeItem) {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisPlayer wrapPlayer(Object nativePlayer) {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisBlock wrapBlock(Object nativeBlock) {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisWorld wrapWorld(Object nativeWorld) {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisLocation unwrapLocation(Object nativeLocation) {
            return null;
        }

        @Override
        public Object nativeLocation(dev.rono.igniscore.api.port.IgnisLocation location) {
            return null;
        }

        @Override
        public void applyCustomModelData(Object nativeItem, int modelData) {
        }

        @Override
        public java.util.OptionalInt readCustomModelData(Object nativeItem) {
            return java.util.OptionalInt.empty();
        }

        @Override
        public void applyItemMeta(Object nativeItem, net.kyori.adventure.text.Component displayName,
                                  java.util.List<net.kyori.adventure.text.Component> lore, String itemModelKey) {
        }

        @Override
        public void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force) {
        }

        @Override
        public void sendMessage(Object nativeSender, net.kyori.adventure.text.Component message) {
        }

        @Override
        public boolean isBlockReplaceable(Object nativeBlock) {
            return false;
        }

        @Override
        public String resolveSoundKey(String bukkitStyleSoundName) {
            return bukkitStyleSoundName;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisInventory createInventory(Object holder, int size,
                                                                            net.kyori.adventure.text.Component title) {
            return null;
        }

        @Override
        public void registerEventListeners(Object listenerRegistry) {
        }

        @Override
        public void registerCommand(String name, Object commandExecutor) {
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisWorld resolveWorld(dev.rono.igniscore.api.port.IgnisLocation location) {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisItem createMaterialItem(String materialKey, int amount) {
            return null;
        }

        @Override
        public void clearBlock(dev.rono.igniscore.api.port.IgnisLocation location) {
        }

        @Override
        public void shutdown() {
        }
    };

    private TestBootloaderSupport() {
    }

    public static final class HighPriorityBootloader implements PlatformBootloader {
        public HighPriorityBootloader() {
        }

        @Override
        public String id() {
            return "test-high";
        }

        @Override
        public PlatformType platformType() {
            return PlatformType.SPIGOT;
        }

        @Override
        public String minecraftVersionRange() {
            return "test";
        }

        @Override
        public int priority() {
            return 200;
        }

        @Override
        public boolean canBoot(Object host) {
            return host != null && !(host instanceof PlatformBootloaderLoaderTest.UnmatchedHost);
        }

        @Override
        public PlatformAdapter boot(Object host) {
            return ADAPTER;
        }
    }

    public static final class LowPriorityBootloader implements PlatformBootloader {
        public LowPriorityBootloader() {
        }

        @Override
        public String id() {
            return "test-low";
        }

        @Override
        public PlatformType platformType() {
            return PlatformType.SPIGOT;
        }

        @Override
        public String minecraftVersionRange() {
            return "test";
        }

        @Override
        public int priority() {
            return 50;
        }

        @Override
        public boolean canBoot(Object host) {
            return host != null && !(host instanceof PlatformBootloaderLoaderTest.UnmatchedHost);
        }

        @Override
        public PlatformAdapter boot(Object host) {
            throw new UnsupportedOperationException("low priority bootloader should not boot");
        }
    }
}
