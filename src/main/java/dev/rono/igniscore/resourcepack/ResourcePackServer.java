package dev.rono.igniscore.resourcepack;

import com.sun.net.httpserver.HttpServer;
import dev.rono.igniscore.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ResourcePackServer {
    private final Main plugin;
    private HttpServer server;

    public ResourcePackServer(Main plugin) {
        this.plugin = plugin;
    }

    public void start(String host, int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/resourcepack.zip", exchange -> {
                File file = new File(plugin.getDataFolder(), "resourcepack.zip");
                if (!file.exists()) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                }
                exchange.close();
            });
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("Resource pack server started on " + host + ":" + port);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start resource pack server: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
