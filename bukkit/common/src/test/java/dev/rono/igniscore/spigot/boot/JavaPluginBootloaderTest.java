package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.support.MockBukkitTestBase;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavaPluginBootloaderTest extends MockBukkitTestBase {

    @Test
    void exposesConfiguredMetadata() {
        TestBootloader bootloader = new TestBootloader();
        assertEquals("test-bootloader", bootloader.id());
        assertEquals(PlatformType.SPIGOT, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(42, bootloader.priority());
    }

    @Test
    void bootsAdapterForJavaPluginHost() {
        TestBootloader bootloader = new TestBootloader();

        assertSame(TestBootloader.ADAPTER, bootloader.boot(plugin));
    }

    @Test
    void rejectsNonJavaPluginHost() {
        assertThrows(IllegalArgumentException.class, () -> new TestBootloader().boot("not-a-plugin"));
    }

    @Test
    void canBootRequiresMatchingRuntimeAndVersion() {
        TestBootloader bootloader = new TestBootloader();
        assertFalse(bootloader.canBoot("not-a-plugin"));
    }

    private static final class TestBootloader extends JavaPluginBootloader {
        static final PlatformAdapter ADAPTER = new PlatformAdapter() {
            @Override
            public PlatformType getPlatformType() {
                return PlatformType.SPIGOT;
            }

            @Override
            public String getMinecraftVersion() {
                return "1.21.1";
            }

            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getGlobal();
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

        private TestBootloader() {
            super("test-bootloader", PlatformType.SPIGOT, "1.21.x", 42, 1, 21);
        }

        @Override
        protected PlatformAdapter createAdapter(JavaPlugin plugin) {
            return ADAPTER;
        }
    }
}
