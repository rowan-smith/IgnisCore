package dev.rono.igniscore.support;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.LoadedExtension;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class TestDefinitions {
    private TestDefinitions() {
    }

    public static BlockDefinition block(String id) {
        return new BlockDefinition(
                id,
                "paper",
                "carrot_on_a_stick",
                Component.text(id),
                List.of(Component.text("test")),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(
                        "combustible", true,
                        "ignition_materials", List.of("FLINT_AND_STEEL", "FIRE_CHARGE", "FLINT")),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                id
        );
    }

    public static BlockDefinition breakableStorage() {
        return new BlockDefinition(
                "storage",
                "paper",
                "carrot_on_a_stick",
                Component.text("storage"),
                List.of(Component.text("test")),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 80, "radius", 4.0),
                Map.of("ticks", 5),
                Map.of("combustible", false),
                Map.of(),
                Map.of(),
                10002,
                false,
                false,
                false,
                "storage"
        );
    }

    public static ItemDefinition item(String id) {
        return new ItemDefinition(
                id,
                "snowball",
                Component.text(id),
                List.of(Component.text("test")),
                Map.of("power", 4.0),
                Map.of(),
                20001,
                id,
                "icon.png"
        );
    }

    public static LoadedExtension<BlockDefinition> loadedBlock(BlockDefinition definition, URLClassLoader classLoader) {
        return loadedBlock(definition, classLoader, manifest(definition.getExtensionId(), "block-extension.yml"));
    }

    public static LoadedExtension<ItemDefinition> loadedItem(ItemDefinition definition, URLClassLoader classLoader) {
        return new LoadedExtension<>(
                manifest(definition.getExtensionId(), "item-extension.yml"),
                new File("test.jar"),
                classLoader,
                definition,
                new ExtensionResources(classLoader)
        );
    }

    public static LoadedExtension<BlockDefinition> loadedBlock(BlockDefinition definition,
                                                               URLClassLoader classLoader,
                                                               ExtensionManifest manifest) {
        return new LoadedExtension<>(
                manifest,
                new File("test.jar"),
                classLoader,
                definition,
                new ExtensionResources(classLoader)
        );
    }

    private static ExtensionManifest manifest(String id, String fileName) {
        return ExtensionManifest.fromStream(
                new ByteArrayInputStream(("id: " + id + "\n").getBytes(StandardCharsets.UTF_8)),
                fileName);
    }
}
