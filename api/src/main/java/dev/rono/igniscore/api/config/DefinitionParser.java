package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefinitionParser {
    private DefinitionParser() {
    }

    public static BlockDefinition parseBlock(YamlConfiguration config, String fallbackId, int modelData, String extensionId) {
        String id = config.getString("id", fallbackId);

        String titleStr = config.getString("display.title", id);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);

        List<String> descStrings = config.getStringList("display.description");
        List<Component> description = new ArrayList<>();
        for (String line : descStrings) {
            description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
        }

        boolean placeable = config.getBoolean("block.placeable", true);
        boolean breakable = config.getBoolean("block.breakable", true);
        String baseMaterial = config.getString("block.base_material", "paper").toLowerCase();
        String renderMaterial = config.getString("block.render_material", "carrot_on_a_stick").toLowerCase();
        Map<String, Object> breakSettings = sectionToMap(config.getConfigurationSection("block.breaking"));

        String top = config.getString("textures.top", id + "-top.png");
        String side = config.getString("textures.side", id + "-side.png");
        String bottom = config.getString("textures.bottom", id + "-bottom.png");
        String side1 = config.isSet("textures.side-1") ? config.getString("textures.side-1") : null;
        String side2 = config.isSet("textures.side-2") ? config.getString("textures.side-2") : null;
        String side3 = config.isSet("textures.side-3") ? config.getString("textures.side-3") : null;
        String side4 = config.isSet("textures.side-4") ? config.getString("textures.side-4") : null;

        String strategy = config.getString("behavior.strategy", "default");
        Map<String, Object> customData = sectionToMap(config.getConfigurationSection("behavior.custom_data"));
        Map<String, Object> interactionSettings = sectionToMap(config.getConfigurationSection("interactions"));

        if (config.contains("behavior.fuse")) {
            customData.putIfAbsent("fuse", config.getInt("behavior.fuse"));
        }
        if (config.contains("behavior.radius")) {
            customData.putIfAbsent("radius", config.getDouble("behavior.radius"));
        }

        if (config.isConfigurationSection("explosion")) {
            if ("default".equals(strategy)) {
                strategy = config.getString("explosion.strategy", "default");
            }
            customData.putIfAbsent("fuse", config.getInt("explosion.fuse", 80));
            customData.putIfAbsent("radius", config.getDouble("explosion.radius", 4.0));

            customData.put("power", config.getDouble("explosion.power", 4.0));
            customData.put("multiplier", config.getDouble("explosion.multiplier", 1.0));
            customData.put("fire", config.getBoolean("explosion.effects.fire", false));
            customData.put("blockDamage", config.getBoolean("explosion.effects.destroy_blocks", true));
            customData.put("screenShake", config.getBoolean("explosion.effects.screen_shake", false));

            if (config.isConfigurationSection("explosion.entity_payload")) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("type", config.getString("explosion.entity_payload.type"));
                payload.put("count", config.getInt("explosion.entity_payload.count", 0));
                payload.put("behavior", config.getString("explosion.entity_payload.behavior", "normal"));
                payload.put("targetPlayers", config.getBoolean("explosion.entity_payload.target_players", false));
                customData.put("entityPayload", payload);
            }
        }

        boolean pulse = config.getBoolean("block_display.animations.pulse", true);
        boolean rotate = config.getBoolean("block_display.animations.rotate", true);
        boolean floatBob = config.getBoolean("block_display.animations.float", true);
        Map<String, Object> displaySettings = sectionToMap(config.getConfigurationSection("block_display"));

        return new BlockDefinition(id, baseMaterial, renderMaterial, title, description, placeable, breakable,
                top, side, bottom, strategy, customData, breakSettings, interactionSettings,
                displaySettings, modelData, rotate, floatBob, pulse, extensionId, side1, side2, side3, side4);
    }

    public static IgnisStrategyDescriptor parseStrategyDescriptor(YamlConfiguration config, ExtensionManifest manifest) {
        String id = config.getString("behavior.strategy", "default");
        String name = config.getString("behavior.strategy_name", manifest.getName());
        String version = config.getString("behavior.strategy_version", manifest.getVersion());
        String author = config.getString("behavior.strategy_author", manifest.getAuthor());
        return IgnisStrategyDescriptor.of(id, name, version, author, manifest.getId());
    }

    public static ItemDefinition parseItem(YamlConfiguration config, String fallbackId, int modelData, String extensionId) {
        String id = config.getString("id", fallbackId);

        String titleStr = config.getString("display.title", id);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);

        List<String> descStrings = config.getStringList("display.description");
        List<Component> description = new ArrayList<>();
        for (String line : descStrings) {
            description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line));
        }

        String baseMaterial = config.getString("item.base_material", "paper").toLowerCase();
        String strategy = config.getString("behavior.strategy", "default");
        String iconTexture = config.getString("textures.icon", "icon.png");
        Map<String, Object> customData = sectionToMap(config.getConfigurationSection("behavior.custom_data"));
        Map<String, Object> interactionSettings = sectionToMap(config.getConfigurationSection("interactions"));

        return new ItemDefinition(id, baseMaterial, title, description, strategy, customData,
                interactionSettings, modelData, extensionId, iconTexture);
    }

    private static Map<String, Object> sectionToMap(org.bukkit.configuration.ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        if (section == null) {
            return map;
        }

        for (String key : section.getKeys(false)) {
            Object val = section.get(key);
            if (val instanceof org.bukkit.configuration.ConfigurationSection subSection) {
                map.put(key, sectionToMap(subSection));
            } else {
                map.put(key, val);
            }
        }
        return map;
    }
}
