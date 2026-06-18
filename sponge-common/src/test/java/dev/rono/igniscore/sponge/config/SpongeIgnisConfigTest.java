package dev.rono.igniscore.sponge.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpongeIgnisConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsDefaultsWhenConfigMissing() {
        SpongeIgnisConfig config = new SpongeIgnisConfig(tempDir.resolve("config.yml"));

        assertEquals("0.0.0.0", config.resourcePackHost());
        assertEquals(8080, config.resourcePackPort());
        assertEquals("http://0.0.0.0:8080/resourcepack.zip", config.resourcePackPublicUrl());
        assertEquals(16, config.performanceSettings().chunkRestoreBlocksPerTick());
        assertEquals(32, config.performanceSettings().visualRefreshBlocksPerTick());
        assertEquals(3, config.performanceSettings().resourcePackRetainCount());
    }

    @Test
    void reloadsUpdatedValuesFromDisk() throws Exception {
        Path configFile = tempDir.resolve("config.yml");
        Files.writeString(configFile, """
                resource-pack:
                  host: "127.0.0.1"
                  port: 9090
                  public-url: "http://127.0.0.1:9090/pack.zip"
                performance:
                  chunk-restore-blocks-per-tick: 8
                  visual-refresh-blocks-per-tick: 4
                  resource-pack-retain-count: 2
                """);

        SpongeIgnisConfig config = new SpongeIgnisConfig(configFile);
        assertEquals("127.0.0.1", config.resourcePackHost());
        assertEquals(9090, config.resourcePackPort());
        assertEquals("http://127.0.0.1:9090/pack.zip", config.resourcePackPublicUrl());
        assertEquals(8, config.performanceSettings().chunkRestoreBlocksPerTick());
        assertEquals(4, config.performanceSettings().visualRefreshBlocksPerTick());
        assertEquals(2, config.performanceSettings().resourcePackRetainCount());
    }
}
