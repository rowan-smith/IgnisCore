package dev.rono.igniscore.event;

import com.google.inject.Singleton;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.event.OnItemClickListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class IgnisEventBusImpl implements IgnisEventBus {
    private final Map<Class<?>, List<Registration<?>>> registrations = new ConcurrentHashMap<>();

    private record Registration<L>(String extensionId, L listener) {
    }

    @Override
    public void subscribe(OnBlockPlaceListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockClickListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockInteractListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockBreakListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockActivateListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockTickListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnBlockTriggerListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(OnItemClickListener listener) {
        subscribe(null, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockPlaceListener listener) {
        register(OnBlockPlaceListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockClickListener listener) {
        register(OnBlockClickListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockInteractListener listener) {
        register(OnBlockInteractListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockBreakListener listener) {
        register(OnBlockBreakListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockActivateListener listener) {
        register(OnBlockActivateListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockTickListener listener) {
        register(OnBlockTickListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnBlockTriggerListener listener) {
        register(OnBlockTriggerListener.class, extensionId, listener);
    }

    @Override
    public void subscribe(String extensionId, OnItemClickListener listener) {
        register(OnItemClickListener.class, extensionId, listener);
    }

    @Override
    public void unsubscribe(Object listener) {
        registrations.values().forEach(list ->
                list.removeIf(registration -> registration.listener() == listener));
    }

    private <L> void register(Class<L> type, String extensionId, L listener) {
        registrations.computeIfAbsent(type, ignored -> new CopyOnWriteArrayList<>())
                .add(new Registration<>(normalizeExtensionId(extensionId), listener));
    }

    private static String normalizeExtensionId(String extensionId) {
        return extensionId == null ? null : extensionId.toLowerCase();
    }

    public <L> void dispatch(String extensionId, Class<L> type, java.util.function.Consumer<L> invocation) {
        List<Registration<?>> listeners = registrations.get(type);
        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        String normalized = normalizeExtensionId(extensionId);
        for (Registration<?> registration : listeners) {
            if (registration.extensionId() != null && !registration.extensionId().equals(normalized)) {
                continue;
            }
            invocation.accept(type.cast(registration.listener()));
        }
    }
}
