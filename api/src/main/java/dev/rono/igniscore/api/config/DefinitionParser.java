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

public final class DefinitionParser {
    private DefinitionParser() {
    }

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
        Map<String, Object> interactionSettings = YamlDefinitions.flattenSection(YamlDefinitions.section(config, "interactions"));

        if (config.containsKey("fuse")) {
            customData.putIfAbsent("fuse", YamlDefinitions.integer(config, "fuse", 80));
        }
        if (config.containsKey("radius")) {
            customData.putIfAbsent("radius", YamlDefinitions.decimal(config, "radius", 4.0));
        }

        Map<String, Object> explosion = YamlDefinitions.section(config, "explosion");
        if (!explosion.isEmpty()) {
            customData.putIfAbsent("fuse", YamlDefinitions.integer(explosion, "fuse", 80));
            customData.putIfAbsent("radius", YamlDefinitions.decimal(explosion, "radius", 4.0));
            customData.put("power", YamlDefinitions.decimal(explosion, "power", 4.0));
            customData.put("multiplier", YamlDefinitions.decimal(explosion, "multiplier", 1.0));

            Map<String, Object> effects = YamlDefinitions.section(explosion, "effects");
            customData.put("fire", YamlDefinitions.bool(effects, "fire", false));
            customData.put("blockDamage", YamlDefinitions.bool(effects, "destroy_blocks", true));
            customData.put("screenShake", YamlDefinitions.bool(effects, "screen_shake", false));

            Map<String, Object> payloadSection = YamlDefinitions.section(explosion, "entity_payload");
            if (!payloadSection.isEmpty()) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("type", YamlDefinitions.string(payloadSection, "type", null));
                payload.put("count", YamlDefinitions.integer(payloadSection, "count", 0));
                payload.put("behavior", YamlDefinitions.string(payloadSection, "behavior", "normal"));
                payload.put("targetPlayers", YamlDefinitions.bool(payloadSection, "target_players", false));
                customData.put("entityPayload", payload);
            }
        }

        Map<String, Object> displaySection = YamlDefinitions.section(config, "block_display");
        Map<String, Object> animations = YamlDefinitions.section(displaySection, "animations");
        boolean pulse = YamlDefinitions.bool(animations, "pulse", true);
        boolean rotate = YamlDefinitions.bool(animations, "rotate", true);
        boolean floatBob = YamlDefinitions.bool(animations, "float", true);

        return new BlockDefinition(id, baseMaterial, renderMaterial, title, description, placeable, breakable,
                top, side, bottom, customData, breakSettings, interactionSettings,
                YamlDefinitions.flattenSection(displaySection), modelData, rotate, floatBob, pulse, extensionId,
                side1, side2, side3, side4);
    }

    public static IgnisStrategyDescriptor parseStrategyDescriptor(ExtensionManifest manifest) {
        return IgnisStrategyDescriptor.of(
                manifest.getId(),
                manifest.getName(),
                manifest.getVersion(),
                manifest.getAuthor(),
                manifest.getId());
    }

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
        Map<String, Object> interactionSettings = YamlDefinitions.flattenSection(YamlDefinitions.section(config, "interactions"));

        return new ItemDefinition(id, baseMaterial, title, description, customData,
                interactionSettings, modelData, extensionId, iconTexture);
    }
}
