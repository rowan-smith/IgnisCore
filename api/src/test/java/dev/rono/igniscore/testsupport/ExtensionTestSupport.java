package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ExtensionTestSupport {
    private ExtensionTestSupport() {
    }

    public static ExtensionManifest loadManifest(Class<?> anchor, String manifestFileName) {
        try (InputStream input = anchor.getResourceAsStream("/" + manifestFileName)) {
            Objects.requireNonNull(input, manifestFileName + " missing from module resources");
            return ExtensionManifest.fromStream(input, manifestFileName);
        } catch (Exception error) {
            throw new IllegalStateException("Failed to load " + manifestFileName, error);
        }
    }

    public static BlockDefinition loadBlockDefinition(Class<?> anchor, String extensionId, int customModelData) {
        try (InputStream input = anchor.getResourceAsStream("/config.yml")) {
            Objects.requireNonNull(input, "config.yml missing from module resources");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
            return DefinitionParser.parseBlock(config, extensionId, customModelData, extensionId);
        } catch (Exception error) {
            throw new IllegalStateException("Failed to load block config.yml", error);
        }
    }

    public static ItemDefinition loadItemDefinition(Class<?> anchor, String extensionId, int customModelData) {
        try (InputStream input = anchor.getResourceAsStream("/config.yml")) {
            Objects.requireNonNull(input, "config.yml missing from module resources");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
            return DefinitionParser.parseItem(config, extensionId, customModelData, extensionId);
        } catch (Exception error) {
            throw new IllegalStateException("Failed to load item config.yml", error);
        }
    }

    public static IgnisStrategyContext noopContext() {
        return new IgnisStrategyContext(null, null, null, null, NoopExtensionSupport.INSTANCE);
    }

    public static IgnisStrategyContext context(ExtensionSupport extensionSupport) {
        return new IgnisStrategyContext(null, null, null, null, extensionSupport);
    }

    public static IgnisStrategyContext context(org.bukkit.plugin.Plugin plugin, ExtensionSupport extensionSupport) {
        return new IgnisStrategyContext(plugin, null, null, null, extensionSupport);
    }
}
