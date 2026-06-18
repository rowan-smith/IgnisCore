package dev.rono.igniscore.sponge.resourcepack;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.resourcepack.ResourcePackBuilder;
import dev.rono.igniscore.resourcepack.ResourcePackFingerprint;
import dev.rono.igniscore.resourcepack.ResourcePackServer;
import dev.rono.igniscore.resourcepack.ResourcePackStorage;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.config.SpongeIgnisConfig;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class SpongeResourcePackService {
    private final SpongePluginContext pluginContext;
    private final SpongeIgnisConfig config;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ResourcePackBuilder packBuilder;
    private final PlatformAdapter platformAdapter;
    private final ResourcePackServer packServer;
    private final int resourcePackRetainCount;

    private final Object buildLock = new Object();
    private volatile boolean buildInProgress;
    private Runnable pendingOnSuccess;
    private Consumer<IOException> pendingOnFailure;
    private String latestHash;
    private String lastBuiltFingerprint;

    @Inject
    public SpongeResourcePackService(SpongePluginContext pluginContext,
                                     SpongeIgnisConfig config,
                                     BlockManager blockManager,
                                     ItemManager itemManager,
                                     BlockExtensionLoader blockExtensionLoader,
                                     ItemExtensionLoader itemExtensionLoader,
                                     ResourcePackBuilder packBuilder,
                                     PlatformAdapter platformAdapter,
                                     IgnisRuntimeHost runtimeHost,
                                     PerformanceSettings performanceSettings) {
        this.pluginContext = pluginContext;
        this.config = config;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.packBuilder = packBuilder;
        this.platformAdapter = platformAdapter;
        this.packServer = new ResourcePackServer(runtimeHost);
        this.resourcePackRetainCount = performanceSettings.resourcePackRetainCount();
    }

    public void buildAndRegister() throws IOException {
        Map<String, BlockDefinition> blocks = Map.copyOf(blockManager.getBlockTypes());
        Map<String, ItemDefinition> items = Map.copyOf(itemManager.getItemTypes());
        String fingerprint = ResourcePackFingerprint.compute(
                blocks, items,
                blockExtensionLoader.getLoadedExtensions(),
                itemExtensionLoader.getLoadedExtensions());
        registerBuiltPack(packBuilder.buildPack(blocks, items));
        lastBuiltFingerprint = fingerprint;
    }

    public void buildAndRegisterAsync(Runnable onSuccess, Consumer<IOException> onFailure) {
        Map<String, BlockDefinition> blocks = Map.copyOf(blockManager.getBlockTypes());
        Map<String, ItemDefinition> items = Map.copyOf(itemManager.getItemTypes());
        String fingerprint = ResourcePackFingerprint.compute(
                blocks, items,
                blockExtensionLoader.getLoadedExtensions(),
                itemExtensionLoader.getLoadedExtensions());

        synchronized (buildLock) {
            if (fingerprint.equals(lastBuiltFingerprint) && latestHash != null) {
                pluginContext.debug("Skipping resource pack rebuild; inputs unchanged.");
                onSuccess.run();
                return;
            }
            if (buildInProgress) {
                pendingOnSuccess = onSuccess;
                pendingOnFailure = onFailure;
                return;
            }
            buildInProgress = true;
        }

        asyncExecutor().execute(() -> {
            try {
                ResourcePackBuilder.PackResult result = packBuilder.buildPack(blocks, items);
                platformAdapter.getScheduler().runGlobal(
                        () -> finishAsyncBuild(fingerprint, result, onSuccess, onFailure));
            } catch (IOException error) {
                platformAdapter.getScheduler().runGlobal(
                        () -> finishAsyncBuildFailure(onFailure, error));
            }
        });
    }

    private void finishAsyncBuild(String fingerprint,
                                  ResourcePackBuilder.PackResult result,
                                  Runnable onSuccess,
                                  Consumer<IOException> onFailure) {
        IOException failure = null;
        try {
            registerBuiltPack(result);
            lastBuiltFingerprint = fingerprint;
        } catch (RuntimeException error) {
            failure = new IOException(error.getMessage(), error);
        }

        Runnable queuedSuccess = null;
        Consumer<IOException> queuedFailure = null;
        synchronized (buildLock) {
            if (failure != null) {
                onFailure.accept(failure);
            } else {
                onSuccess.run();
            }

            if (pendingOnSuccess != null) {
                queuedSuccess = pendingOnSuccess;
                queuedFailure = pendingOnFailure;
                pendingOnSuccess = null;
                pendingOnFailure = null;
            }
            buildInProgress = false;
        }

        if (queuedSuccess != null) {
            buildAndRegisterAsync(queuedSuccess, queuedFailure);
        }
    }

    private void finishAsyncBuildFailure(Consumer<IOException> onFailure, IOException error) {
        Runnable queuedSuccess = null;
        Consumer<IOException> queuedFailure = null;
        synchronized (buildLock) {
            onFailure.accept(error);
            if (pendingOnSuccess != null) {
                queuedSuccess = pendingOnSuccess;
                queuedFailure = pendingOnFailure;
                pendingOnSuccess = null;
                pendingOnFailure = null;
            }
            buildInProgress = false;
        }

        if (queuedSuccess != null) {
            buildAndRegisterAsync(queuedSuccess, queuedFailure);
        }
    }

    private void registerBuiltPack(ResourcePackBuilder.PackResult result) {
        latestHash = result.getHash();
        packServer.registerPack(latestHash, result.getFile());
        cleanupOldPacks(result.getFile().toPath().getParent());
        pluginContext.debug("Resource pack generated successfully! Hash: " + latestHash);
    }

    private void cleanupOldPacks(Path packsDirectory) {
        if (packsDirectory == null) {
            return;
        }

        try {
            Set<String> retainedHashes = ResourcePackStorage.determineRetainedHashes(
                    packsDirectory, latestHash, resourcePackRetainCount);
            packServer.retainOnly(latestHash, retainedHashes);
            int deleted = ResourcePackStorage.deleteUnretainedPacks(packsDirectory, retainedHashes);
            if (deleted > 0) {
                pluginContext.debug("Cleaned up " + deleted + " old resource pack file(s).");
            }
        } catch (IOException error) {
            pluginContext.logger().warn("Failed to clean up old resource packs: " + error.getMessage());
        }
    }

    public void reloadConfiguration() {
        config.reload();
        restartServer();
    }

    public void restartServer() {
        stopServer();
        startServer();
    }

    public void startServer() {
        packServer.start(config.resourcePackHost(), config.resourcePackPort());
    }

    public void stopServer() {
        packServer.stop();
    }

    public void requestPack(ServerPlayer player) {
        String url = config.resourcePackPublicUrl();
        if (url == null || url.isEmpty()) {
            platformAdapter.sendMessage(player, pluginContext.message(
                    "<red>Resource pack URL not configured in config.yml"));
            return;
        }

        if (latestHash != null) {
            platformAdapter.sendResourcePack(player, versionedUrl(url), hexToBytes(latestHash), false);
        } else {
            platformAdapter.sendResourcePack(player, url, null, false);
        }
        platformAdapter.sendMessage(player, pluginContext.message("<green>Resource pack requested."));
    }

    public String getLatestHash() {
        return latestHash;
    }

    private Executor asyncExecutor() {
        if (platformAdapter instanceof SpongePlatformAdapter spongeAdapter) {
            return spongeAdapter.game().server().scheduler().executor(spongeAdapter.container());
        }
        return Runnable::run;
    }

    private String versionedUrl(String url) {
        if (url.endsWith(".zip")) {
            return url.replace(".zip", "_" + latestHash + ".zip");
        }
        return url;
    }

    private byte[] hexToBytes(String value) {
        int length = value.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
                    + Character.digit(value.charAt(i + 1), 16));
        }
        return data;
    }
}
