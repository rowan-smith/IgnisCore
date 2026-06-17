package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts parsed YAML maps into API model types and strategy descriptors.
 *
 * <p>Prefer {@link YamlDefinitions} as the public entry point; this class holds the shared
 * parsing logic used by the core loader and tests.</p>
 */
public final class DefinitionParser {
    private DefinitionParser() {
    }

    /**
     * Builds a {@link BlockDefinition} from a {@code config.yml} root map.
     *
     * @param config parsed YAML root
     * @param fallbackId id used when the config omits {@code id}
     * @param modelData custom model data assigned by the loader
     * @param extensionId manifest strategy id for this extension
     * @return fully populated block definition
     */
    public static BlockDefinition parseBlock(Map<String, Object> config, String fallbackId, int modelData, String extensionId) {
        String id = YamlDefinitions.string(config, "id", fallbackId);

        Map<String, Object> display = YamlDefinitions.section(config, "display");
        Component title = YamlDefinitions.component(YamlDefinitions.string(display, "title", id));
        List<Component> description = new ArrayList<>();
        for (String line : YamlDefinitions.stringList(display, "description")) {
            description.add(YamlDefinitions.component(line));
        }

        Map<String, Object> block = YamlDefinitions.section(config, "block");
        boolean placeable = YamlDefinitions.bool(block, "placeable", true);
        boolean breakable = YamlDefinitions.bool(block, "breakable", true);
        String baseMaterial = YamlDefinitions.string(block, "base_material", "paper").toLowerCase();
        String renderMaterial = YamlDefinitions.string(block, "render_material", "carrot_on_a_stick").toLowerCase();
        Map<String, Object> breakSettings = YamlDefinitions.flattenSection(YamlDefinitions.section(block, "breaking"));

        Map<String, Object> textures = YamlDefinitions.section(config, "textures");
        String top = YamlDefinitions.string(textures, "top", id + "-top.png");
        String side = YamlDefinitions.string(textures, "side", id + "-side.png");
        String bottom = YamlDefinitions.string(textures, "bottom", id + "-bottom.png");
        String side1 = textures.containsKey("side-1") ? YamlDefinitions.string(textures, "side-1", null) : null;
        String side2 = textures.containsKey("side-2") ? YamlDefinitions.string(textures, "side-2", null) : null;
        String side3 = textures.containsKey("side-3") ? YamlDefinitions.string(textures, "side-3", null) : null;
        String side4 = textures.containsKey("side-4") ? YamlDefinitions.string(textures, "side-4", null) : null;

        Map<String, Object> customData = new HashMap<>(YamlDefinitions.flattenSection(YamlDefinitions.section(config, "custom_data")));
        Map<String, Object> behaviorSettings = new HashMap<>(YamlDefinitions.section(config, "behavior"));
        Map<String, Object> interactionSettings = YamlDefinitions.flattenSection(YamlDefinitions.section(config, "interactions"));

        Map<String, Object> displaySection = YamlDefinitions.section(config, "block_display");
        Map<String, Object> animations = YamlDefinitions.section(displaySection, "animations");
        boolean pulse = YamlDefinitions.bool(animations, "pulse", true);
        boolean rotate = YamlDefinitions.bool(animations, "rotate", true);
        boolean floatBob = YamlDefinitions.bool(animations, "float", true);

        return new BlockDefinition(id, baseMaterial, renderMaterial, title, description, placeable, breakable,
                top, side, bottom, customData, breakSettings, behaviorSettings, interactionSettings,
                YamlDefinitions.flattenSection(displaySection), modelData, rotate, floatBob, pulse, extensionId,
                side1, side2, side3, side4);
    }

    /**
     * Builds a strategy registry descriptor from extension manifest metadata.
     *
     * @param manifest parsed {@code *-extension.yml}
     * @return descriptor used to register the extension strategy
     */
    public static IgnisStrategyDescriptor parseStrategyDescriptor(ExtensionManifest manifest) {
        return IgnisStrategyDescriptor.of(
                manifest.getId(),
                manifest.getName(),
                manifest.getVersion(),
                manifest.getAuthor(),
                manifest.getId());
    }

    /**
     * Builds an {@link ItemDefinition} from a {@code config.yml} root map.
     *
     * @param config parsed YAML root
     * @param fallbackId id used when the config omits {@code id}
     * @param modelData custom model data assigned by the loader
     * @param extensionId manifest strategy id for this extension
     * @return fully populated item definition
     */
    public static ItemDefinition parseItem(Map<String, Object> config, String fallbackId, int modelData, String extensionId) {
        String id = YamlDefinitions.string(config, "id", fallbackId);

        Map<String, Object> display = YamlDefinitions.section(config, "display");
        Component title = YamlDefinitions.component(YamlDefinitions.string(display, "title", id));
        List<Component> description = new ArrayList<>();
        for (String line : YamlDefinitions.stringList(display, "description")) {
            description.add(YamlDefinitions.component(line));
        }

        Map<String, Object> item = YamlDefinitions.section(config, "item");
        String baseMaterial = YamlDefinitions.string(item, "base_material", "paper").toLowerCase();
        Map<String, Object> textures = YamlDefinitions.section(config, "textures");
        String iconTexture = YamlDefinitions.string(textures, "icon", "icon.png");
        Map<String, Object> customData = YamlDefinitions.flattenSection(YamlDefinitions.section(config, "custom_data"));
        Map<String, Object> behaviorSettings = new HashMap<>(YamlDefinitions.section(config, "behavior"));
        Map<String, Object> interactionSettings = YamlDefinitions.flattenSection(YamlDefinitions.section(config, "interactions"));

        return new ItemDefinition(id, baseMaterial, title, description, customData, behaviorSettings,
                interactionSettings, modelData, extensionId, iconTexture);
    }
}
