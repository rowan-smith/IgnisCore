package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

public final class SpongeIgnisScheduler implements IgnisScheduler {
    private final Scheduler scheduler;
    private final PluginContainer plugin;

    public SpongeIgnisScheduler(Scheduler scheduler, PluginContainer plugin) {
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public IgnisTask runLater(IgnisLocation location, Runnable task, long delayTicks) {
        return wrap(submit(task, delayTicks, 0));
    }

    @Override
    public IgnisTask runRepeating(IgnisLocation location, Runnable task, long delayTicks, long periodTicks) {
        return wrap(submit(task, delayTicks, periodTicks));
    }

    @Override
    public void runGlobal(Runnable task) {
        submit(task, 0, 0);
    }

    @Override
    public void runGlobalLater(Runnable task, long delayTicks) {
        submit(task, delayTicks, 0);
    }

    private ScheduledTask submit(Runnable task, long delayTicks, long periodTicks) {
        Task.Builder builder = Task.builder()
                .plugin(plugin)
                .execute(ignored -> task.run());
        if (delayTicks > 0) {
            builder.delay(Ticks.of(delayTicks));
        }
        if (periodTicks > 0) {
            builder.interval(Ticks.of(periodTicks));
        }
        return scheduler.submit(builder.build());
    }

    private static IgnisTask wrap(ScheduledTask task) {
        return new IgnisTask() {
            @Override
            public void cancel() {
                task.cancel();
            }

            @Override
            public boolean isCancelled() {
                return task.isCancelled();
            }
        };
    }
}
