package dev.rono.igniscore.resourcepack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.rono.igniscore.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class ResourcePackServer {
    private final Main plugin;
    private HttpServer server;
    private final Map<String, File> packs = new ConcurrentHashMap<>();
    private String latestPackId;

    public ResourcePackServer(Main plugin) {
        this.plugin = plugin;
    }

    public void start(String host, int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            
            // Handler for both latest and versioned packs
            HttpHandler handler = exchange -> {
                String path = exchange.getRequestURI().getPath();
                File file = null;

                if (path.equals("/resourcepack.zip")) {
                    if (latestPackId != null) {
                        file = packs.get(latestPackId);
                    }
                } else if (path.startsWith("/resourcepack_") && path.endsWith(".zip")) {
                    String id = path.substring("/resourcepack_".length(), path.length() - ".zip".length());
                    file = packs.get(id);
                }

                if (file == null || !file.exists()) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                    return;
                }

                // HTTP Headers for Zip download
                exchange.getResponseHeaders().set("Content-Type", "application/zip");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
                exchange.getResponseHeaders().set("Pragma", "no-cache");
                exchange.getResponseHeaders().set("Expires", "0");

                exchange.sendResponseHeaders(200, file.length());

                try (exchange; OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                    os.flush();
                } catch (IOException e) {
                    plugin.getLogger().warning("Error streaming resource pack: " + e.getMessage());
                }
            };

            server.createContext("/", handler);
            
            // Use a cached thread pool to handle concurrent downloads efficiently
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            plugin.getLogger().info("Resource pack server started on " + host + ":" + port);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start resource pack server: " + e.getMessage());
        }
    }

    public void registerPack(String id, File file) {
        this.packs.put(id, file);
        this.latestPackId = id;
    }

    public String getLatestPackId() {
        return latestPackId;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
