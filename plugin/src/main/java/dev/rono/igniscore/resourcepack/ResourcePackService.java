package dev.rono.igniscore.resourcepack;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.platform.PlatformHooks;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ResourcePackService {
    private final Main plugin;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ResourcePackBuilder packBuilder;
    private final ResourcePackServer packServer;
    private final PlatformHooks platformHooks;

    private String latestHash;

    @Inject
    public ResourcePackService(Main plugin,
                               BlockManager blockManager,
                               ItemManager itemManager,
                               ResourcePackBuilder packBuilder,
                               PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.packBuilder = packBuilder;
        this.platformHooks = platformHooks;
        this.packServer = new ResourcePackServer(plugin);
    }

    public void buildAndRegister() throws IOException {
        ResourcePackBuilder.PackResult result = packBuilder.buildPack(
                blockManager.getBlockTypes(), itemManager.getItemTypes());
        latestHash = result.getHash();
        packServer.registerPack(latestHash, result.getFile());
        plugin.getLogger().info("Resource pack generated successfully! Hash: " + latestHash);
    }

    public void reloadConfiguration() {
        plugin.reloadConfig();
        restartServer();
    }

    public void reloadBuildAndRegister() throws IOException {
        plugin.reloadConfig();
        buildAndRegister();
        restartServer();
    }

    public void restartServer() {
        stopServer();
        startServer();
    }

    public void startServer() {
        String host = plugin.getConfig().getString("resource-pack.host", "0.0.0.0");
        int port = plugin.getConfig().getInt("resource-pack.port", 8080);
        packServer.start(host, port);
    }

    public void stopServer() {
        packServer.stop();
    }

    public void requestPack(Player player) {
        String url = plugin.getConfig().getString("resource-pack.public-url");
        if (url == null || url.isEmpty()) {
            platformHooks.sendMessage(player, plugin.message("<red>Resource pack URL not configured in config.yml"));
            return;
        }

        if (latestHash != null) {
            platformHooks.sendResourcePack(player, versionedUrl(url), hexToBytes(latestHash), false);
        } else {
            platformHooks.sendResourcePack(player, url, null, false);
        }
        platformHooks.sendMessage(player, plugin.message("<green>Resource pack requested."));
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
