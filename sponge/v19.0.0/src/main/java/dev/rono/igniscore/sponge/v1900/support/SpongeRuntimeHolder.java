package dev.rono.igniscore.sponge.v1900.support;

import org.spongepowered.api.Server;

public final class SpongeRuntimeHolder {
    private static volatile Server server;

    private SpongeRuntimeHolder() {
    }

    public static void bind(Server boundServer) {
        server = boundServer;
    }

    public static Server server() {
        Server current = server;
        if (current == null) {
            throw new IllegalStateException("Sponge runtime has not been initialized");
        }
        return current;
    }

    public static void clear() {
        server = null;
    }
}
