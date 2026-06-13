package dev.rono.igniscore.resourcepack;

import com.google.inject.Inject;
import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackFingerprint;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.platform.PlatformHooks;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class ResourcePackService {
    private final IgnisPluginContext pluginContext;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ResourcePackBuilder packBuilder;
    private final ResourcePackServer packServer;
    private final PlatformHooks platformHooks;

    private final Object buildLock = new Object();
    private volatile boolean buildInProgress;
    private Runnable pendingOnSuccess;
    private Consumer<IOException> pendingOnFailure;
    private String latestHash;
    private String lastBuiltFingerprint;

    @Inject
    public ResourcePackService(IgnisPluginContext pluginContext,
                               BlockManager blockManager,
                               ItemManager itemManager,
                               BlockExtensionLoader blockExtensionLoader,
                               ItemExtensionLoader itemExtensionLoader,
                               ResourcePackBuilder packBuilder,
                               PlatformHooks platformHooks,
                               IgnisRuntimeHost runtimeHost) {
        this.pluginContext = pluginContext;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.packBuilder = packBuilder;
        this.platformHooks = platformHooks;
        this.packServer = new ResourcePackServer(runtimeHost);
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

        var plugin = pluginContext.plugin();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ResourcePackBuilder.PackResult result = packBuilder.buildPack(blocks, items);
                plugin.getServer().getScheduler().runTask(plugin, () -> finishAsyncBuild(fingerprint, result, onSuccess, onFailure));
            } catch (IOException error) {
                plugin.getServer().getScheduler().runTask(plugin, () -> finishAsyncBuildFailure(onFailure, error));
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
        pluginContext.debug("Resource pack generated successfully! Hash: " + latestHash);
    }

    public void reloadConfiguration() {
        pluginContext.plugin().reloadConfig();
        restartServer();
    }

    public void restartServer() {
        stopServer();
        startServer();
    }

    public void startServer() {
        String host = pluginContext.plugin().getConfig().getString("resource-pack.host", "0.0.0.0");
        int port = pluginContext.plugin().getConfig().getInt("resource-pack.port", 8080);
        packServer.start(host, port);
    }

    public void stopServer() {
        packServer.stop();
    }

    public void requestPack(Player player) {
        String url = pluginContext.plugin().getConfig().getString("resource-pack.public-url");
        if (url == null || url.isEmpty()) {
            platformHooks.sendMessage(player, pluginContext.message("<red>Resource pack URL not configured in config.yml"));
            return;
        }

        if (latestHash != null) {
            platformHooks.sendResourcePack(player, versionedUrl(url), hexToBytes(latestHash), false);
        } else {
            platformHooks.sendResourcePack(player, url, null, false);
        }
        platformHooks.sendMessage(player, pluginContext.message("<green>Resource pack requested."));
    }

    public String getLatestHash() {
        return latestHash;
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
