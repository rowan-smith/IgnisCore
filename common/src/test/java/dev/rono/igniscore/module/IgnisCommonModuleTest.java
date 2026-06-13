package dev.rono.igniscore.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.core.IgnisSchedulerProvider;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class IgnisCommonModuleTest {

    @Test
    void bindsIgnisSchedulerFromPlatformAdapter() {
        CommonTestSupport.ImmediateIgnisScheduler scheduler = new CommonTestSupport.ImmediateIgnisScheduler();
        PlatformAdapter adapter = schedulerPlatformAdapter(scheduler);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PlatformAdapter.class).toInstance(adapter);
                bind(IgnisScheduler.class).toProvider(IgnisSchedulerProvider.class);
            }
        });

        assertSame(scheduler, injector.getInstance(IgnisScheduler.class));
    }

    private static PlatformAdapter schedulerPlatformAdapter(IgnisScheduler scheduler) {
        PlatformAdapter delegate = CommonTestSupport.platformAdapter(null, java.nio.file.Path.of("."));
        return new PlatformAdapter() {
            @Override
            public dev.rono.igniscore.api.port.PlatformType getPlatformType() {
                return delegate.getPlatformType();
            }

            @Override
            public String getMinecraftVersion() {
                return delegate.getMinecraftVersion();
            }

            @Override
            public java.util.logging.Logger getLogger() {
                return delegate.getLogger();
            }

            @Override
            public java.nio.file.Path getDataDirectory() {
                return delegate.getDataDirectory();
            }

            @Override
            public IgnisScheduler getScheduler() {
                return scheduler;
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisItem wrapItem(Object nativeItem) {
                return delegate.wrapItem(nativeItem);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisPlayer wrapPlayer(Object nativePlayer) {
                return delegate.wrapPlayer(nativePlayer);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisBlock wrapBlock(Object nativeBlock) {
                return delegate.wrapBlock(nativeBlock);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisWorld wrapWorld(Object nativeWorld) {
                return delegate.wrapWorld(nativeWorld);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisLocation unwrapLocation(Object nativeLocation) {
                return delegate.unwrapLocation(nativeLocation);
            }

            @Override
            public Object nativeLocation(dev.rono.igniscore.api.port.IgnisLocation location) {
                return delegate.nativeLocation(location);
            }

            @Override
            public void applyCustomModelData(Object nativeItem, int modelData) {
                delegate.applyCustomModelData(nativeItem, modelData);
            }

            @Override
            public java.util.OptionalInt readCustomModelData(Object nativeItem) {
                return delegate.readCustomModelData(nativeItem);
            }

            @Override
            public void applyItemMeta(Object nativeItem, net.kyori.adventure.text.Component displayName,
                                      java.util.List<net.kyori.adventure.text.Component> lore, String itemModelKey) {
                delegate.applyItemMeta(nativeItem, displayName, lore, itemModelKey);
            }

            @Override
            public void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force) {
                delegate.sendResourcePack(nativePlayer, url, hash, force);
            }

            @Override
            public void sendMessage(Object nativeSender, net.kyori.adventure.text.Component message) {
                delegate.sendMessage(nativeSender, message);
            }

            @Override
            public boolean isBlockReplaceable(Object nativeBlock) {
                return delegate.isBlockReplaceable(nativeBlock);
            }

            @Override
            public String resolveSoundKey(String bukkitStyleSoundName) {
                return delegate.resolveSoundKey(bukkitStyleSoundName);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisInventory createInventory(Object holder, int size,
                                                                                net.kyori.adventure.text.Component title) {
                return delegate.createInventory(holder, size, title);
            }

            @Override
            public void registerEventListeners(Object listenerRegistry) {
                delegate.registerEventListeners(listenerRegistry);
            }

            @Override
            public void registerCommand(String name, Object commandExecutor) {
                delegate.registerCommand(name, commandExecutor);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisWorld resolveWorld(
                    dev.rono.igniscore.api.port.IgnisLocation location) {
                return delegate.resolveWorld(location);
            }

            @Override
            public dev.rono.igniscore.api.port.IgnisItem createMaterialItem(String materialKey, int amount) {
                return delegate.createMaterialItem(materialKey, amount);
            }

            @Override
            public void clearBlock(dev.rono.igniscore.api.port.IgnisLocation location) {
                delegate.clearBlock(location);
            }

            @Override
            public void shutdown() {
                delegate.shutdown();
            }
        };
    }
}
