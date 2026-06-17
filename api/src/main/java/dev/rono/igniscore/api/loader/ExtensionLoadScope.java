package dev.rono.igniscore.api.loader;

/**
 * Thread-local scope used while an extension strategy is constructed so event bus
 * subscriptions without an explicit extension id are scoped to the loading extension.
 */
public final class ExtensionLoadScope {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private ExtensionLoadScope() {
    }

    public static <T> T call(String extensionId, Callable<T> action) {
        CURRENT.set(normalize(extensionId));
        try {
            return action.call();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Extension load scope failed", exception);
        } finally {
            CURRENT.remove();
        }
    }

    public static void run(String extensionId, Runnable action) {
        call(extensionId, () -> {
            action.run();
            return null;
        });
    }

    public static String current() {
        return CURRENT.get();
    }

    private static String normalize(String extensionId) {
        return extensionId == null ? null : extensionId.toLowerCase();
    }

    @FunctionalInterface
    public interface Callable<T> {
        T call() throws Exception;
    }
}
