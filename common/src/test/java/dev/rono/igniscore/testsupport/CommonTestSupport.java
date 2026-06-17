package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.loader.LoadedExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public final class CommonTestSupport {
    private CommonTestSupport() {
    }

    public static LoadedExtension<BlockDefinition> loadedBlock(BlockDefinition definition) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new java.net.URL[]{new File(".").toURI().toURL()});
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("""
                        id: %s
                        name: Test
                        version: 1.0.0
                        api-version: 1.0.0
                        strategy: dev.example.Strategy
                        """.formatted(definition.getExtensionId()).getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");
        return new LoadedExtension<>(manifest, new File("test.jar"), classLoader, definition, new ExtensionResources(classLoader));
    }

    public static IgnisRuntimeHost runtimeHost(java.nio.file.Path dataDirectory) {
        return new IgnisRuntimeHost() {
            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getLogger("igniscore-test");
            }

            @Override
            public java.nio.file.Path getDataDirectory() {
                return dataDirectory;
            }

            @Override
            public java.io.InputStream openBundledResource(String resourcePath) {
                return null;
            }

            @Override
            public java.net.URI getDeploymentLocation() {
                return dataDirectory.toUri();
            }

            @Override
            public ClassLoader getExtensionParentClassLoader() {
                return CommonTestSupport.class.getClassLoader();
            }

            @Override
            public void debug(String message) {
            }
        };
    }

    public static dev.rono.igniscore.api.port.PlatformAdapter platformAdapter(
            dev.rono.igniscore.api.port.IgnisWorld world,
            java.nio.file.Path dataDirectory) {
        return new dev.rono.igniscore.api.port.PlatformAdapter() {
            @Override
            public dev.rono.igniscore.api.port.PlatformType getPlatformType() {
                return dev.rono.igniscore.api.port.PlatformType.SPIGOT;
            }

            @Override
            public String getMinecraftVersion() {
                return "test";
            }

            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getLogger("igniscore-test");
            }

            @Override
            public java.nio.file.Path getDataDirectory() {
                return dataDirectory;
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
                return world;
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
                return world;
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
    }

    public static final class RecordingBlockVisualRenderer implements dev.rono.igniscore.api.port.BlockVisualRenderer {
        private final java.util.List<dev.rono.igniscore.api.port.IgnisLocation> staticDisplays = new java.util.ArrayList<>();
        private final java.util.List<dev.rono.igniscore.api.model.RuntimeBlockInstance> animatedDisplays = new java.util.ArrayList<>();
        private int removedStaticCount;
        private int removedAnimatedCount;

        @Override
        public void spawnAnimatedDisplay(dev.rono.igniscore.api.model.RuntimeBlockInstance instance) {
            animatedDisplays.add(instance);
        }

        @Override
        public Object spawnStaticDisplay(dev.rono.igniscore.api.port.IgnisLocation location, BlockDefinition definition) {
            staticDisplays.add(location);
            return "display-" + staticDisplays.size();
        }

        @Override
        public void updateAnimation(dev.rono.igniscore.api.model.RuntimeBlockInstance instance) {
        }

        @Override
        public void removeDisplay(dev.rono.igniscore.api.model.RuntimeBlockInstance instance) {
            removedAnimatedCount++;
        }

        @Override
        public void removeStaticDisplay(Object nativeDisplay) {
            removedStaticCount++;
        }

        public java.util.List<dev.rono.igniscore.api.port.IgnisLocation> staticDisplays() {
            return java.util.List.copyOf(staticDisplays);
        }

        public int animatedDisplayCount() {
            return animatedDisplays.size();
        }

        public int removedStaticCount() {
            return removedStaticCount;
        }

        public int removedAnimatedCount() {
            return removedAnimatedCount;
        }
    }

    public static final class ImmediateIgnisScheduler implements dev.rono.igniscore.api.port.IgnisScheduler {
        private Runnable lastRepeatingTask;

        @Override
        public dev.rono.igniscore.api.port.IgnisTask runLater(
                dev.rono.igniscore.api.port.IgnisLocation location, Runnable task, long delayTicks) {
            task.run();
            return cancelledTask();
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisTask runRepeating(
                dev.rono.igniscore.api.port.IgnisLocation location, Runnable task, long delayTicks, long periodTicks) {
            lastRepeatingTask = task;
            return cancelledTask();
        }

        public void runLastRepeatingTask() {
            if (lastRepeatingTask != null) {
                lastRepeatingTask.run();
            }
        }

        @Override
        public void runGlobal(Runnable task) {
            task.run();
        }

        @Override
        public void runGlobalLater(Runnable task, long delayTicks) {
            task.run();
        }

        private static dev.rono.igniscore.api.port.IgnisTask cancelledTask() {
            return new dev.rono.igniscore.api.port.IgnisTask() {
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
    }

    public static final class DeferredIgnisScheduler implements dev.rono.igniscore.api.port.IgnisScheduler {
        private final java.util.ArrayDeque<Runnable> pending = new java.util.ArrayDeque<>();

        @Override
        public dev.rono.igniscore.api.port.IgnisTask runLater(
                dev.rono.igniscore.api.port.IgnisLocation location, Runnable task, long delayTicks) {
            pending.add(task);
            return cancelledTask();
        }

        @Override
        public dev.rono.igniscore.api.port.IgnisTask runRepeating(
                dev.rono.igniscore.api.port.IgnisLocation location, Runnable task, long delayTicks, long periodTicks) {
            pending.add(task);
            return cancelledTask();
        }

        @Override
        public void runGlobal(Runnable task) {
            task.run();
        }

        @Override
        public void runGlobalLater(Runnable task, long delayTicks) {
            pending.add(task);
        }

        public void runPending() {
            while (!pending.isEmpty()) {
                pending.poll().run();
            }
        }

        private static dev.rono.igniscore.api.port.IgnisTask cancelledTask() {
            return new dev.rono.igniscore.api.port.IgnisTask() {
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
    }
}
