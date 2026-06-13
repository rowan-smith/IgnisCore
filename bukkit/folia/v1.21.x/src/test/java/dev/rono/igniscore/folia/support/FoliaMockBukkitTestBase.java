package dev.rono.igniscore.folia.support;

import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.plugin.PluginManagerMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings({"deprecation", "removal"})
public abstract class FoliaMockBukkitTestBase {
    protected ServerMock server;
    protected World world;
    protected JavaPlugin plugin;

    @BeforeEach
    void startMockBukkit() throws IOException {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("world");

        PluginDescriptionFile description = new PluginDescriptionFile(
                "IgnisCore",
                "1.0.0",
                FoliaTestPlugin.class.getName());
        JavaPluginLoader loader = new JavaPluginLoader(server);
        File dataFolder = Files.createTempDirectory("igniscore-folia-test-data").toFile();
        File pluginFile = ((PluginManagerMock) server.getPluginManager()).createTemporaryPluginFile("IgnisCore");

        plugin = new FoliaTestPlugin(loader, description, dataFolder, pluginFile);
        ((PluginManagerMock) server.getPluginManager()).registerLoadedPlugin(plugin);
        server.getPluginManager().enablePlugin(plugin);
    }

    @AfterEach
    void stopMockBukkit() {
        MockBukkit.unmock();
    }
}
