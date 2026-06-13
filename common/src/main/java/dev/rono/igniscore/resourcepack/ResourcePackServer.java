package dev.rono.igniscore.resourcepack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ResourcePackServer {
    private final Logger logger;
    private HttpServer server;
    private ExecutorService executor;
    private final Map<String, File> packs = new ConcurrentHashMap<>();
    private String latestPackId;

    public ResourcePackServer(IgnisRuntimeHost host) {
        this.logger = host.getLogger();
    }

    public void start(String host, int port) {
        stop();

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

                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                } catch (IOException e) {
                    logger.warning("Error streaming resource pack: " + e.getMessage());
                }
            };

            server.createContext("/", handler);
            executor = Executors.newFixedThreadPool(2);
            server.setExecutor(executor);
            server.start();
            logger.info("Resource pack server started on " + host + ":" + port);
        } catch (IOException e) {
            logger.severe("Failed to start resource pack server: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public void registerPack(String id, File file) {
        packs.put(id, file);
        latestPackId = id;
    }

    public void retainOnly(String latestId, Set<String> retainedIds) {
        if (retainedIds == null || retainedIds.isEmpty()) {
            packs.clear();
            latestPackId = latestId;
            return;
        }
        packs.keySet().removeIf(id -> !retainedIds.contains(id));
        latestPackId = latestId;
    }

    public String getLatestPackId() {
        return latestPackId;
    }

    public String getPackUrl(String host, int port) {
        return "http://" + host + ":" + port + "/resourcepack.zip";
    }

    public String getPackUrl(String host, int port, String packId) {
        return "http://" + host + ":" + port + "/resourcepack_" + packId + ".zip";
    }
}
