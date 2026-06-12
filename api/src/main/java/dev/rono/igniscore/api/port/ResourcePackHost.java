package dev.rono.igniscore.api.port;

import java.io.IOException;

/**
 * Host-facing resource pack lifecycle used by the shared runtime.
 */
public interface ResourcePackHost {

    void buildAndRegister() throws IOException;

    void startServer();

    void stopServer();

    void reloadConfiguration();
}
