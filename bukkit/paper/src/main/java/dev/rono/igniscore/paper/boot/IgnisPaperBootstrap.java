package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.paper.command.PaperCommandSupport;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;

public final class IgnisPaperBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        PaperCommandSupport.installBootstrap(context.getPluginMeta(), context.getLifecycleManager());
    }
}
