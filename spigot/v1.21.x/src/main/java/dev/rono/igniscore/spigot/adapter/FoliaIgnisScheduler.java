package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Region-aware scheduler for Folia servers.
 */
public final class FoliaIgnisScheduler implements IgnisScheduler {
    private final JavaPlugin plugin;
    private final Object regionScheduler;
    private final Object globalRegionScheduler;

    public FoliaIgnisScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.regionScheduler = invokeStaticNoArgs(Bukkit.class, "getRegionScheduler");
        this.globalRegionScheduler = invokeStaticNoArgs(Bukkit.class, "getGlobalRegionScheduler");
    }

    @Override
    public IgnisTask runLater(IgnisLocation location, Runnable task, long delayTicks) {
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        Object scheduled = invoke(regionScheduler, "runDelayed",
                plugin, bukkitLocation, toConsumer(task), delayTicks);
        return wrap(scheduled);
    }

    @Override
    public IgnisTask runRepeating(IgnisLocation location, Runnable task, long delayTicks, long periodTicks) {
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        Object scheduled = invoke(regionScheduler, "runAtFixedRate",
                plugin, bukkitLocation, toConsumer(task), delayTicks, periodTicks);
        return wrap(scheduled);
    }

    @Override
    public void runGlobal(Runnable task) {
        invoke(globalRegionScheduler, "run", plugin, toConsumer(task));
    }

    @Override
    public void runGlobalLater(Runnable task, long delayTicks) {
        invoke(globalRegionScheduler, "runDelayed", plugin, toConsumer(task), delayTicks);
    }

    private static Consumer<Object> toConsumer(Runnable task) {
        return scheduled -> task.run();
    }

    private static IgnisTask wrap(Object scheduledTask) {
        return new IgnisTask() {
            @Override
            public void cancel() {
                invokeNoArgs(scheduledTask, "cancel");
            }

            @Override
            public boolean isCancelled() {
                Object result = invokeNoArgs(scheduledTask, "isCancelled");
                return result instanceof Boolean bool && bool;
            }
        };
    }

    private static Object invokeStaticNoArgs(Class<?> type, String method) {
        try {
            return type.getMethod(method).invoke(null);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Failed to invoke static " + method + " on " + type.getName(), error);
        }
    }

    private static Object invoke(Object target, String method, Object... args) {
        try {
            Method resolved = findMethod(target.getClass(), method, args);
            return resolved.invoke(target, args);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Failed to invoke " + method + " on Folia scheduler", error);
        }
    }

    private static Method findMethod(Class<?> type, String method, Object... args) throws NoSuchMethodException {
        for (Method candidate : type.getMethods()) {
            if (!candidate.getName().equals(method) || candidate.getParameterCount() != args.length) {
                continue;
            }
            Class<?>[] parameterTypes = candidate.getParameterTypes();
            boolean compatible = true;
            for (int i = 0; i < args.length; i++) {
                if (!parameterTypes[i].isInstance(args[i])) {
                    compatible = false;
                    break;
                }
            }
            if (compatible) {
                return candidate;
            }
        }
        throw new NoSuchMethodException(method);
    }

    private static Object invokeNoArgs(Object target, String method) {
        try {
            return target.getClass().getMethod(method).invoke(target);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Failed to invoke " + method + " on Folia scheduled task", error);
        }
    }
}
