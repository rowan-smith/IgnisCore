package dev.rono.igniscore.sponge.service;

import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.ExtensionLoadResult;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.sponge.SpongePluginContext;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpongeExtensionReloadServiceTest {
    private RecordingExtensionBootstrap bootstrap;
    private RecordingPlatformAdapter platformAdapter;
    private SpongeExtensionReloadService reloadService;
    private final List<String> messages = new ArrayList<>();

    @BeforeEach
    void setUp() {
        bootstrap = new RecordingExtensionBootstrap();
        platformAdapter = new RecordingPlatformAdapter();
        reloadService = new SpongeExtensionReloadService(
                new SpongePluginContext(new TestSpongePluginHost()),
                bootstrap,
                platformAdapter);
    }

    @Test
    void reloadAsyncRunsPrepareLoadAndCommit() {
        AtomicBoolean success = new AtomicBoolean();
        reloadService.reloadAsync(ExtensionReloadScope.ALL, new Object(), null, null, () -> success.set(true));

        assertEquals(1, bootstrap.prepareCount);
        assertEquals(1, bootstrap.loadFreshCount);
        assertEquals(0, bootstrap.commitCount);

        platformAdapter.runSyncTasks();
        assertEquals(1, bootstrap.commitCount);
        assertTrue(success.get());
    }

    @Test
    void rejectsConcurrentReloadRequests() {
        Object sender = new Object();
        reloadService.reloadAsync(ExtensionReloadScope.ALL, sender, "start", "done", () -> {});
        reloadService.reloadAsync(ExtensionReloadScope.ALL, sender, "start", "done", () -> {});

        assertEquals(1, bootstrap.prepareCount);
        assertTrue(messages.stream().anyMatch(message -> message.contains("already in progress")));
    }

    private final class RecordingPlatformAdapter implements PlatformAdapter {
        private final List<Runnable> asyncTasks = new ArrayList<>();
        private final List<Runnable> syncTasks = new ArrayList<>();

        @Override
        public void sendMessage(Object nativeSender, Component message) {
            messages.add(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(message));
        }

        @Override
        public IgnisScheduler getScheduler() {
            return new IgnisScheduler() {
                @Override
                public IgnisTask runLater(dev.rono.igniscore.api.port.IgnisLocation location, Runnable task, long delayTicks) {
                    syncTasks.add(task);
                    return cancelledTask();
                }

                @Override
                public IgnisTask runRepeating(dev.rono.igniscore.api.port.IgnisLocation location, Runnable task,
                                              long delayTicks, long periodTicks) {
                    syncTasks.add(task);
                    return cancelledTask();
                }

                @Override
                public void runGlobal(Runnable task) {
                    syncTasks.add(task);
                }

                @Override
                public void runGlobalLater(Runnable task, long delayTicks) {
                    syncTasks.add(task);
                }
            };
        }

        void runAsyncTasks() {
            List<Runnable> pending = List.copyOf(asyncTasks);
            asyncTasks.clear();
            pending.forEach(Runnable::run);
        }

        void runSyncTasks() {
            List<Runnable> pending = List.copyOf(syncTasks);
            syncTasks.clear();
            pending.forEach(Runnable::run);
        }

        private static IgnisTask cancelledTask() {
            return new IgnisTask() {
                private boolean cancelled;

                @Override
                public void cancel() {
                    cancelled = true;
                }

                @Override
                public boolean isCancelled() {
                    return cancelled;
                }
            };
        }

        @Override public dev.rono.igniscore.api.port.PlatformType getPlatformType() { return dev.rono.igniscore.api.port.PlatformType.SPONGE; }
        @Override public String getMinecraftVersion() { return "test"; }
        @Override public Logger getLogger() { return Logger.getLogger("test"); }
        @Override public Path getDataDirectory() { return Path.of("."); }
        @Override public dev.rono.igniscore.api.port.IgnisItem wrapItem(Object nativeItem) { return null; }
        @Override public dev.rono.igniscore.api.port.IgnisPlayer wrapPlayer(Object nativePlayer) { return null; }
        @Override public dev.rono.igniscore.api.port.IgnisBlock wrapBlock(Object nativeBlock) { return null; }
        @Override public dev.rono.igniscore.api.port.IgnisWorld wrapWorld(Object nativeWorld) { return null; }
        @Override public dev.rono.igniscore.api.port.IgnisLocation unwrapLocation(Object nativeLocation) { return null; }
        @Override public Object nativeLocation(dev.rono.igniscore.api.port.IgnisLocation location) { return null; }
        @Override public void applyCustomModelData(Object nativeItem, int modelData) {}
        @Override public OptionalInt readCustomModelData(Object nativeItem) { return OptionalInt.empty(); }
        @Override public void applyItemMeta(Object nativeItem, Component displayName, List<Component> lore, String itemModelKey) {}
        @Override public void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force) {}
        @Override public boolean isBlockReplaceable(Object nativeBlock) { return false; }
        @Override public String resolveSoundKey(String bukkitStyleSoundName) { return bukkitStyleSoundName; }
        @Override public dev.rono.igniscore.api.port.IgnisInventory createInventory(Object holder, int size, Component title) { return null; }
        @Override public void registerEventListeners(Object listenerRegistry) {}
        @Override public void registerCommand(String name, Object commandExecutor) {}
        @Override public dev.rono.igniscore.api.port.IgnisWorld resolveWorld(dev.rono.igniscore.api.port.IgnisLocation location) { return null; }
        @Override public dev.rono.igniscore.api.port.IgnisItem createMaterialItem(String materialKey, int amount) { return null; }
        @Override public void clearBlock(dev.rono.igniscore.api.port.IgnisLocation location) {}
        @Override public void shutdown() {}
    }

    private static final class RecordingExtensionBootstrap extends ExtensionBootstrap {
        int prepareCount;
        int loadFreshCount;
        int commitCount;

        RecordingExtensionBootstrap() {
            super(null, null, null, null, null, null, null);
        }

        @Override
        public void prepareForReload(ExtensionReloadScope scope) {
            prepareCount++;
        }

        @Override
        public ExtensionLoadResult loadFresh(ExtensionReloadScope scope) {
            loadFreshCount++;
            return new ExtensionLoadResult(List.of(), List.of());
        }

        @Override
        public void commitReload(ExtensionReloadScope scope, ExtensionLoadResult result) {
            commitCount++;
        }
    }

    private static final class TestSpongePluginHost implements dev.rono.igniscore.sponge.SpongePluginHost {
        @Override
        public org.spongepowered.plugin.PluginContainer container() {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.spongepowered.api.Game game() {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.apache.logging.log4j.Logger getLogger() {
            return org.apache.logging.log4j.LogManager.getLogger("test");
        }

        @Override
        public PlatformAdapter platformAdapter() {
            return null;
        }
    }
}
