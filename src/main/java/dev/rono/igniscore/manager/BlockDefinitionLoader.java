package dev.rono.igniscore.manager;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BlockDefinitionLoader {
    private final Main plugin;

    public BlockDefinitionLoader(Main plugin) {
        this.plugin = plugin;
    }

    public Map<String, BlockDefinition> loadDefinitions(List<String> registeredIds) {
        Map<String, BlockDefinition> definitions = new LinkedHashMap<>();
        int modelData = 10001;

        File blocksFolder = new File(plugin.getDataFolder(), "blocks");
        if (!blocksFolder.exists()) {
            blocksFolder.mkdirs();
        }

        for (String id : registeredIds) {
            BlockDefinition def = null;

            // 1. Search internal
            def = loadFromResource(id, modelData);

            // 2. Search external if not found internal
            if (def == null) {
                File folder = new File(blocksFolder, id);
                if (folder.exists() && folder.isDirectory()) {
                    def = loadFromFolder(folder, modelData);
                }
            }

            if (def != null) {
                definitions.put(def.getId(), def);
                modelData++;
            } else {
                plugin.getLogger().severe("CRITICAL: Block ID '" + id + "' is registered in config.yml but could not be found internally or in the blocks folder!");
            }
        }

        return definitions;
    }

    private BlockDefinition loadFromResource(String id, int modelData) {
        try (InputStream in = plugin.getResource("blocks/" + id + "/config.yml")) {
            if (in == null) return null;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
            return loadFromConfig(config, id, modelData);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load internal block " + id + ": " + e.getMessage());
            return null;
        }
    }

    private BlockDefinition loadFromFolder(File folder, int modelData) {
        File configFile = new File(folder, "config.yml");
        if (!configFile.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return loadFromConfig(config, folder.getName(), modelData);
    }

    private BlockDefinition loadFromConfig(YamlConfiguration config, String folderName, int modelData) {
        String id = config.getString("id", folderName);
        
        String titleStr = config.getString("display.title", id);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);
        
        List<String> descStrings = config.getStringList("display.description");
        List<Component> description = new ArrayList<>();
        for (String s : descStrings) {
            description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
        }
        
        boolean placeable = config.getBoolean("block.placeable", true);
        boolean breakable = config.getBoolean("block.breakable", true);
        String baseMaterial = config.getString("block.base_material", "paper").toLowerCase();
        String renderMaterial = config.getString("block.render_material", "carrot_on_a_stick").toLowerCase();
        Map<String, Object> breakSettings = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection breakSection = config.getConfigurationSection("block.breaking");
        if (breakSection != null) {
            breakSettings.putAll(sectionToMap(breakSection));
        }

        String top = config.getString("textures.top", id + "-top.png");
        String side = config.getString("textures.side", id + "-side.png");
        String bottom = config.getString("textures.bottom", id + "-bottom.png");
        
        String strategy = config.getString("behavior.strategy", "default");
        int fuse = config.getInt("behavior.fuse", 80);
        double radius = config.getDouble("behavior.radius", 4.0);
        
        Map<String, Object> customData = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection customSection = config.getConfigurationSection("behavior.custom_data");
        if (customSection != null) {
            customData.putAll(sectionToMap(customSection));
        }
        Map<String, Object> interactionSettings = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection interactionSection = config.getConfigurationSection("interactions");
        if (interactionSection != null) {
            interactionSettings.putAll(sectionToMap(interactionSection));
        }
        
        // Backward compatibility for old configs if any
        if (config.isConfigurationSection("explosion")) {
            if (strategy.equals("default")) strategy = config.getString("explosion.strategy", "default");
            if (fuse == 80) fuse = config.getInt("explosion.fuse", 80);
            if (radius == 4.0) radius = config.getDouble("explosion.radius", 4.0);
            
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
        Map<String, Object> displaySettings = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection displaySection = config.getConfigurationSection("block_display");
        if (displaySection != null) {
            displaySettings.putAll(sectionToMap(displaySection));
        }

        return new BlockDefinition(id, baseMaterial, renderMaterial, title, description, placeable, breakable,
                                 top, side, bottom, strategy, fuse, radius, 
                                 customData, breakSettings, interactionSettings, displaySettings, modelData, rotate, floatBob, pulse);
    }

    private Map<String, Object> sectionToMap(org.bukkit.configuration.ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
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
