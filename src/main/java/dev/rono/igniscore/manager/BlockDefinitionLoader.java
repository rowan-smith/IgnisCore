package dev.rono.igniscore.manager;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDefinitionLoader {
    private final Main plugin;

    public BlockDefinitionLoader(Main plugin) {
        this.plugin = plugin;
    }

    public Map<String, BlockDefinition> loadDefinitions() {
        Map<String, BlockDefinition> definitions = new HashMap<>();
        File blocksFolder = new File(plugin.getDataFolder(), "blocks");
        
        if (!blocksFolder.exists()) {
            if (blocksFolder.mkdirs()) {
                // Copy default blocks from resources if they don't exist
                saveDefaultBlock("nuke", true);
                saveDefaultBlock("spider-storm", false);
            }
        }

        File[] folders = blocksFolder.listFiles(File::isDirectory);
        if (folders != null) {
            java.util.Arrays.sort(folders, java.util.Comparator.comparing(File::getName));
            int modelData = 1;
            for (File folder : folders) {
                BlockDefinition def = loadFromFolder(folder, modelData++);
                if (def != null) {
                    definitions.put(def.getId(), def);
                }
            }
        }
        
        return definitions;
    }

    private void saveDefaultBlock(String id, boolean useUnderscore) {
        String separator = useUnderscore ? "_" : "-";
        saveFile("blocks/" + id + "/config.yml");
        saveFile("blocks/" + id + "/textures/" + id + separator + "top.png");
        saveFile("blocks/" + id + "/textures/" + id + separator + "side.png");
        saveFile("blocks/" + id + "/textures/" + id + separator + "bottom.png");
    }

    private void saveFile(String resourcePath) {
        File outFile = new File(plugin.getDataFolder(), resourcePath);
        if (outFile.exists()) return;
        
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                plugin.getLogger().warning("Failed to create directory: " + parent.getAbsolutePath());
            }
        }
        
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return;
            try (FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save " + resourcePath + " to " + outFile.getName());
        }
    }

    private BlockDefinition loadFromFolder(File folder, int modelData) {
        File configFile = new File(folder, "config.yml");
        if (!configFile.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String id = config.getString("id", folder.getName());
        
        String titleStr = config.getString("display.title", id);
        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);
        
        List<String> descStrings = config.getStringList("display.description");
        List<Component> description = new ArrayList<>();
        for (String s : descStrings) {
            description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
        }
        
        boolean placeable = config.getBoolean("block.placeable", true);
        boolean breakable = config.getBoolean("block.breakable", true);
        String baseMaterial = config.getString("block.base_material", "tnt").toLowerCase();

        String top = config.getString("textures.top", id + "-top.png");
        String side = config.getString("textures.side", id + "-side.png");
        String bottom = config.getString("textures.bottom", id + "-bottom.png");
        
        String strategy = config.getString("behavior.strategy", "default");
        int fuse = config.getInt("behavior.fuse", 80);
        double radius = config.getDouble("behavior.radius", 4.0);
        
        Map<String, Object> customData = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection customSection = config.getConfigurationSection("behavior.custom_data");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                customData.put(key, customSection.get(key));
            }
        }
        
        // Backward compatibility for old configs if any
        if (config.isConfigurationSection("explosion")) {
            if (strategy.equals("default")) strategy = config.getString("explosion.strategy", "default");
            if (fuse == 80) fuse = config.getInt("explosion.fuse", 80);
            if (radius == 4.0) radius = config.getDouble("explosion.radius", 4.0);
            
            // Collect other explosion data into customData
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

        return new BlockDefinition(id, baseMaterial, title, description, placeable, breakable,
                                 top, side, bottom, strategy, fuse, radius, 
                                 customData, modelData, rotate, floatBob, pulse);
    }
}
