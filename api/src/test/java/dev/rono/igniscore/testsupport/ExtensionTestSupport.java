package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.config.YamlDefinitions;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

import java.io.InputStream;
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
            return DefinitionParser.parseBlock(
                    YamlDefinitions.loadMap(input),
                    extensionId,
                    customModelData,
                    extensionId);
        } catch (Exception error) {
            throw new IllegalStateException("Failed to load block config.yml", error);
        }
    }

    public static ItemDefinition loadItemDefinition(Class<?> anchor, String extensionId, int customModelData) {
        try (InputStream input = anchor.getResourceAsStream("/config.yml")) {
            Objects.requireNonNull(input, "config.yml missing from module resources");
            return DefinitionParser.parseItem(
                    YamlDefinitions.loadMap(input),
                    extensionId,
                    customModelData,
                    extensionId);
        } catch (Exception error) {
            throw new IllegalStateException("Failed to load item config.yml", error);
        }
    }

    public static IgnisStrategyContext noopContext() {
        return new IgnisStrategyContext(null, null, null, null, null, null, null, null,
                NoopExtensionSupport.INSTANCE, NoopEventBus.INSTANCE);
    }

    public static IgnisStrategyContext context(ExtensionSupport extensionSupport) {
        return new IgnisStrategyContext(null, null, null, null, null, null, null, null,
                extensionSupport, NoopEventBus.INSTANCE);
    }
}
