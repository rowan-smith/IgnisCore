package dev.rono.igniscore.api.port;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Host-facing resource pack lifecycle used by the shared runtime.
 */
public interface ResourcePackHost {

    void buildAndRegister() throws IOException;

    default void buildAndRegisterAsync(Runnable onSuccess, Consumer<IOException> onFailure) {
        try {
            buildAndRegister();
            onSuccess.run();
        } catch (IOException error) {
            onFailure.accept(error);
        }
    }

    void startServer();

    void stopServer();

    void reloadConfiguration();
}
