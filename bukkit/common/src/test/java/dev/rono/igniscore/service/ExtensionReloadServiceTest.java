package dev.rono.igniscore.service;

import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.ExtensionLoadResult;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.support.MockBukkitTestBase;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionReloadServiceTest extends MockBukkitTestBase {
    private RecordingExtensionBootstrap bootstrap;
    private RecordingPlatformHooks hooks;
    private ExtensionReloadService reloadService;

    @BeforeEach
    void setUpService() {
        bootstrap = new RecordingExtensionBootstrap();
        hooks = new RecordingPlatformHooks();
        reloadService = new ExtensionReloadService(
                new IgnisPluginContext(plugin),
                bootstrap,
                hooks);
    }

    @Test
    void reloadAsyncRunsPrepareSyncLoadAsyncAndCommitSync() {
        AtomicBoolean success = new AtomicBoolean();
        reloadService.reloadAsync(ExtensionReloadScope.ALL, null, null, null, () -> success.set(true));

        assertEquals(1, bootstrap.prepareCount);
        assertEquals(0, bootstrap.loadFreshCount);
        assertEquals(0, bootstrap.commitCount);

        runAsyncTasks();
        assertEquals(1, bootstrap.loadFreshCount);
        assertEquals(0, bootstrap.commitCount);

        runSyncTasks();
        assertEquals(1, bootstrap.commitCount);
        assertTrue(success.get());
    }

    @Test
    void rejectsConcurrentReloadRequests() {
        CommandSender sender = server.addPlayer("admin").getPlayer();

        reloadService.reloadAsync(ExtensionReloadScope.ALL, sender, "start", "done", () -> {});
        reloadService.reloadAsync(ExtensionReloadScope.ALL, sender, "start", "done", () -> {});

        assertEquals(1, bootstrap.prepareCount);
        assertTrue(hooks.messages.stream().anyMatch(message -> message.contains("already in progress")));

        runAsyncTasks();
        runSyncTasks();
    }

    @Test
    void sendsProgressAndSuccessMessages() {
        CommandSender sender = server.addPlayer("admin").getPlayer();

        reloadService.reloadAsync(ExtensionReloadScope.BLOCKS, sender, "<yellow>Loading", "<green>Done", () -> {});

        assertTrue(hooks.messages.stream().anyMatch(message -> message.contains("Loading")));
        runAsyncTasks();
        runSyncTasks();
        assertTrue(hooks.messages.stream().anyMatch(message -> message.contains("Done")));
    }

    private void runAsyncTasks() {
        BukkitSchedulerMock scheduler = (BukkitSchedulerMock) server.getScheduler();
        scheduler.waitAsyncTasksFinished();
    }

    private void runSyncTasks() {
        BukkitSchedulerMock scheduler = (BukkitSchedulerMock) server.getScheduler();
        scheduler.performTicks(1);
    }

    private static final class RecordingExtensionBootstrap extends ExtensionBootstrap {
        int prepareCount;
        int loadFreshCount;
        int commitCount;

        RecordingExtensionBootstrap() {
            super(null, null, null, null, null, null, null, null);
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

    private static final class RecordingPlatformHooks implements dev.rono.igniscore.platform.PlatformHooks {
        final List<String> messages = new ArrayList<>();

        @Override
        public dev.rono.igniscore.platform.PlatformType getPlatformType() {
            return dev.rono.igniscore.platform.PlatformType.SPIGOT;
        }

        @Override
        public void applyCustomModelData(org.bukkit.inventory.ItemStack item, int modelData) {
        }

        @Override
        public java.util.OptionalInt readCustomModelData(org.bukkit.inventory.ItemStack item) {
            return java.util.OptionalInt.empty();
        }

        @Override
        public void applyItemMeta(org.bukkit.inventory.meta.ItemMeta meta, Component displayName,
                                  List<Component> lore, org.bukkit.NamespacedKey itemModel) {
        }

        @Override
        public void sendResourcePack(org.bukkit.entity.Player player, String url, byte[] hash, boolean force) {
        }

        @Override
        public void sendMessage(CommandSender sender, Component message) {
            messages.add(net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(message));
        }

        @Override
        public boolean isBlockReplaceable(org.bukkit.block.Block block) {
            return false;
        }

        @Override
        public org.bukkit.NamespacedKey getSoundKey(org.bukkit.Sound sound) {
            return org.bukkit.NamespacedKey.minecraft("test");
        }
    }
}
