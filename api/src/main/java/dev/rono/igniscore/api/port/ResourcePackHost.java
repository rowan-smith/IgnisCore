package dev.rono.igniscore.api.port;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Host-facing resource pack lifecycle used by the shared runtime.
 *
 * <p>Builds pack assets, serves them over HTTP when configured, and exposes
 * hooks to reload configuration without restarting the server.</p>
 */
public interface ResourcePackHost {

    /**
     * Builds the resource pack and registers it with the runtime for distribution.
     *
     * @throws IOException when pack assembly or file I/O fails
     */
    void buildAndRegister() throws IOException;

    /**
     * Builds and registers the pack on a background thread, invoking callbacks on completion.
     *
     * <p>Default implementation runs {@link #buildAndRegister()} synchronously and
     * dispatches success or failure to the supplied callbacks.</p>
     *
     * @param onSuccess run when build and registration succeed
     * @param onFailure run with the I/O error when build fails
     */
    default void buildAndRegisterAsync(Runnable onSuccess, Consumer<IOException> onFailure) {
        try {
            buildAndRegister();
            onSuccess.run();
        } catch (IOException error) {
            onFailure.accept(error);
        }
    }

    /**
     * Starts the embedded HTTP server that serves the resource pack.
     */
    void startServer();

    /**
     * Stops the embedded HTTP server and releases listening sockets.
     */
    void stopServer();

    /**
     * Reloads pack-related configuration from disk without a full rebuild.
     */
    void reloadConfiguration();
}
