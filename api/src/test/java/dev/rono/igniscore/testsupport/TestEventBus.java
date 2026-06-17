package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.event.BlockActivateEvent;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.loader.ExtensionLoadScope;
import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public final class TestEventBus implements IgnisEventBus {
    private final Map<Class<?>, List<Registration<?>>> registrations = new ConcurrentHashMap<>();

    private record Registration<L>(String extensionId, L listener) {
    }

    public static TestContext createContext() {
        TestEventBus bus = new TestEventBus();
        BehaviorTestSupport.TestContext base = BehaviorTestSupport.createContext(bus);
        return new TestContext(base, bus);
    }

    public static <T extends IgnisStrategy> T activate(Supplier<T> strategyFactory, String extensionId) {
        return ExtensionLoadScope.call(extensionId, () -> {
            T strategy = strategyFactory.get();
            bindDescriptor(strategy, extensionId);
            return strategy;
        });
    }

    public static void bindDescriptor(IgnisStrategy strategy, String extensionId) {
        if (strategy instanceof AbstractIgnisStrategy abstractStrategy) {
            abstractStrategy.bindDescriptor(IgnisStrategyDescriptor.of(
                    extensionId, extensionId, "1.0.0", "test"));
        }
    }

    public void fireBlockPlace(BlockPlaceEvent event, String extensionId) {
        dispatch(extensionId, OnBlockPlaceListener.class, listener -> listener.onBlockPlace(event));
    }

    public void fireBlockActivate(BlockActivateEvent event, String extensionId) {
        dispatch(extensionId, OnBlockActivateListener.class, listener -> listener.onBlockActivate(event));
    }

    public void fireBlockTrigger(BlockTriggerEvent event, String extensionId) {
        dispatch(extensionId, OnBlockTriggerListener.class, listener -> listener.onBlockTrigger(event));
    }

    public void fireItemClick(ItemClickEvent event, String extensionId) {
        dispatch(extensionId, OnItemClickListener.class, listener -> listener.onItemClick(event));
    }

    private <L> void dispatch(String extensionId, Class<L> type, java.util.function.Consumer<L> invocation) {
        List<Registration<?>> listeners = registrations.get(type);
        if (listeners == null) {
            return;
        }
        String normalized = extensionId.toLowerCase();
        for (Registration<?> registration : listeners) {
            if (registration.extensionId() != null && !registration.extensionId().equals(normalized)) {
                continue;
            }
            invocation.accept(type.cast(registration.listener()));
        }
    }

    private <L> void register(Class<L> type, String extensionId, L listener) {
        registrations.computeIfAbsent(type, ignored -> new CopyOnWriteArrayList<>())
                .add(new Registration<>(extensionId == null ? null : extensionId.toLowerCase(), listener));
    }

    @Override
    public void subscribe(OnBlockPlaceListener listener) {
        register(OnBlockPlaceListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockClickListener listener) {
        register(OnBlockClickListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockInteractListener listener) {
        register(OnBlockInteractListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockBreakListener listener) {
        register(OnBlockBreakListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockActivateListener listener) {
        register(OnBlockActivateListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockTickListener listener) {
        register(OnBlockTickListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnBlockTriggerListener listener) {
        register(OnBlockTriggerListener.class, ExtensionLoadScope.current(), listener);
    }

    @Override
    public void subscribe(OnItemClickListener listener) {
        register(OnItemClickListener.class, ExtensionLoadScope.current(), listener);
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

    public record TestContext(BehaviorTestSupport.TestContext base, TestEventBus eventBus) {
        public dev.rono.igniscore.api.strategy.IgnisStrategyContext context() {
            return base.context();
        }

        public BehaviorTestSupport.RecordingIgnisWorld world() {
            return base.world();
        }
    }
}
